/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2022 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.squidbridge.annotations;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewParam;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.rules.ExternalDescriptionLoader;

/**
 * @deprecated since 2.7 without alternative.
 *
 * Responsibility of loading rule metadata and defining rule definitions is now on (custom) plugin side.
 * SQALE model is not supported by SonarQube anymore. Consequently, deprecated annotation {@link SqaleSubCharacteristic} is
 * ignored and won't have any effect.
 *
 * Other SQALE annotations related to remediation functions are also deprecated. The following methods from SQ 6.7 LTS API should
 * be used instead:
 * <ul>
 * <li>org.sonar.api.server.rule.RulesDefinition.NewRule.setDebtRemediationFunction(DebtRemediationFunction)</li>
 * <li>org.sonar.api.server.rule.RulesDefinition.NewRule.setGapDescription(String)</li>
 * </ul>
 *
 * <p>Deprecated documentation:</p>
 *
 * Utility class which helps setting up an implementation of {@link RulesDefinition} with a list of
 * rule classes annotated with {@link Rule}, {@link RuleProperty} and SQALE annotations:
 * <ul>
 * <li>{@link SqaleSubCharacteristic}</li>
 * <li>Exactly one of:
 * <ul>
 * <li>{@link SqaleConstantRemediation}</li>
 * <li>{@link SqaleLinearRemediation}</li>
 * <li>{@link SqaleLinearWithOffsetRemediation}</li>
 * </ul>
 * </li>
 * </ul>
 * Names and descriptions are also retrieved based on the legacy SonarQube conventions:
 * <ul>
 * <li>Rule names and rule property descriptions can be defined in a property file:
 * /org/sonar/l10n/[languageKey].properties</li>
 * <li>HTML rule descriptions can be defined in individual resources:
 * /org/sonar/l10n/[languageKey]/rules/[repositoryKey]/ruleKey.html</li>
 * </ul>
 *
 */
@Deprecated
public class AnnotationBasedRulesDefinition {

  private final NewRepository repository;
  private final String languageKey;
  private final ExternalDescriptionLoader externalDescriptionLoader;

  /**
   * Adds annotated rule classes to an instance of NewRepository. Fails if one the classes has no SQALE annotation.
   */
  public static void load(NewRepository repository, String languageKey, Iterable<Class> ruleClasses) {
    new AnnotationBasedRulesDefinition(repository, languageKey).addRuleClasses(true, ruleClasses);
  }

  public AnnotationBasedRulesDefinition(NewRepository repository, String languageKey) {
    this.repository = repository;
    this.languageKey = languageKey;
    String externalDescriptionBasePath = String.format("/org/sonar/l10n/%s/rules/%s", languageKey, repository.key());
    this.externalDescriptionLoader = new ExternalDescriptionLoader(externalDescriptionBasePath);
  }

  public void addRuleClasses(Iterable<Class> ruleClasses) {
    addRuleClasses(true, ruleClasses);
  }

  public void addRuleClasses(boolean failIfNoExplicitKey, Iterable<Class> ruleClasses) {
    new RulesDefinitionAnnotationLoader().load(repository, Iterables.toArray(ruleClasses, Class.class));
    List<NewRule> newRules = Lists.newArrayList();
    for (Class<?> ruleClass : ruleClasses) {
      NewRule rule = newRule(ruleClass, failIfNoExplicitKey);
      externalDescriptionLoader.addHtmlDescription(rule);
      rule.setTemplate(AnnotationUtils.getAnnotation(ruleClass, RuleTemplate.class) != null);
      try {
        setupRemediationFunction(rule, ruleClass);
      } catch (RuntimeException e) {
        throw new IllegalArgumentException("Could not setup SQALE model on " + ruleClass, e);
      }
      newRules.add(rule);
    }
    setupExternalNames(newRules);
  }

  @VisibleForTesting
  NewRule newRule(Class<?> ruleClass, boolean failIfNoExplicitKey) {
    Rule ruleAnnotation = AnnotationUtils.getAnnotation(ruleClass, Rule.class);
    if (ruleAnnotation == null) {
      throw new IllegalArgumentException("No Rule annotation was found on " + ruleClass);
    }
    String ruleKey = ruleAnnotation.key();
    if (StringUtils.isEmpty(ruleKey)) {
      if (failIfNoExplicitKey) {
        throw new IllegalArgumentException("No key is defined in Rule annotation of " + ruleClass);
      }
      ruleKey = ruleClass.getCanonicalName();
    }
    NewRule rule = repository.rule(ruleKey);
    if (rule == null) {
      throw new IllegalStateException("No rule was created for " + ruleClass + " in " + repository);
    }
    return rule;
  }

  private void setupExternalNames(Collection<NewRule> rules) {
    URL resource = AnnotationBasedRulesDefinition.class.getResource("/org/sonar/l10n/" + languageKey + ".properties");
    if (resource == null) {
      return;
    }
    ResourceBundle bundle = ResourceBundle.getBundle("org.sonar.l10n." + languageKey, Locale.ENGLISH);
    for (NewRule rule : rules) {
      String baseKey = "rule." + repository.key() + "." + rule.key();
      String nameKey = baseKey + ".name";
      if (bundle.containsKey(nameKey)) {
        rule.setName(bundle.getString(nameKey));
      }
      for (NewParam param : rule.params()) {
        String paramDescriptionKey = baseKey + ".param." + param.key();
        if (bundle.containsKey(paramDescriptionKey)) {
          param.setDescription(bundle.getString(paramDescriptionKey));
        }
      }
    }
  }

  private static void setupRemediationFunction(NewRule rule, Class<?> ruleClass) {
    SqaleConstantRemediation constant = AnnotationUtils.getAnnotation(ruleClass, SqaleConstantRemediation.class);
    SqaleLinearRemediation linear = AnnotationUtils.getAnnotation(ruleClass, SqaleLinearRemediation.class);
    SqaleLinearWithOffsetRemediation linearWithOffset = AnnotationUtils.getAnnotation(ruleClass, SqaleLinearWithOffsetRemediation.class);

    Set<Annotation> remediations = Sets.newHashSet(constant, linear, linearWithOffset);
    if (Iterables.size(Iterables.filter(remediations, Predicates.notNull())) > 1) {
      throw new IllegalArgumentException("Found more than one SQALE remediation annotations on " + ruleClass);
    }

    if (constant != null) {
      rule.setDebtRemediationFunction(rule.debtRemediationFunctions().constantPerIssue(constant.value()));
    }
    if (linear != null) {
      rule.setDebtRemediationFunction(rule.debtRemediationFunctions().linear(linear.coeff()));
      rule.setGapDescription(linear.effortToFixDescription());
    }
    if (linearWithOffset != null) {
      rule.setDebtRemediationFunction(
          rule.debtRemediationFunctions().linearWithOffset(linearWithOffset.coeff(), linearWithOffset.offset()));
      rule.setGapDescription(linearWithOffset.effortToFixDescription());
    }
  }
}
