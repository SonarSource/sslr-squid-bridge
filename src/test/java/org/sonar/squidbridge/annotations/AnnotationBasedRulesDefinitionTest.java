/*
 * SSLR Squid Bridge
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.squidbridge.annotations;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.junit.Test;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.debt.DebtRemediationFunction.Type;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.Param;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.SubCharacteristics;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import static org.fest.assertions.Assertions.assertThat;

public class AnnotationBasedRulesDefinitionTest {

  private static final String REPO_KEY = "repoKey";
  private static final String LANGUAGE_KEY_WITH_RESOURCE_BUNDLE = "languageKey";

  private RulesDefinition.Context context = new RulesDefinition.Context();

  @Test
  public void no_class_to_add() throws Exception {
    assertThat(buildRepository().rules()).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void class_without_rule_annotation() throws Exception {
    class NotRuleClass {
    }
    buildSingleRuleRepository(NotRuleClass.class);
  }

  @Test
  public void rule_annotation_data() throws Exception {

    @Rule(key = "key1", name = "name1", description = "description1", tags = "mytag")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("10min")
    class RuleClass {
      @RuleProperty(key = "param1Key", description = "param1 description")
      public String param1 = "x";
    }

    RulesDefinition.Rule rule = buildSingleRuleRepository(RuleClass.class);
    assertThat(rule.key()).isEqualTo("key1");
    assertThat(rule.name()).isEqualTo("name1");
    assertThat(rule.htmlDescription()).isEqualTo("description1");
    assertThat(rule.markdownDescription()).isNull();
    assertThat(rule.tags()).containsOnly("mytag");
    assertThat(rule.params()).hasSize(1);
    assertParam(rule.params().get(0), "param1Key", "param1 description");
  }

  @Test
  public void external_names_and_descriptions() throws Exception {
  
    @Rule(key = "ruleWithExternalInfo")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("10min")
    class RuleClass {
      @RuleProperty(key = "param1Key")
      public String param1 = "x";
      @RuleProperty
      public String param2 = "x";
    }
  
    RulesDefinition.Rule rule = buildSingleRuleRepository(RuleClass.class);
    assertThat(rule.key()).isEqualTo("ruleWithExternalInfo");
    assertThat(rule.name()).isEqualTo("external name for ruleWithExternalInfo");
    assertThat(rule.htmlDescription()).isEqualTo("description for ruleWithExternalInfo");
    assertThat(rule.params()).hasSize(2);
    assertParam(rule.params().get(0), "param1Key", "description for param1");
    assertParam(rule.params().get(1), "param2", null);
  }

  @Test(expected = IllegalStateException.class)
  public void no_name_and_no_resource_bundle() throws Exception {
    @Rule(key = "ruleWithExternalInfo")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("10min")
    class RuleClass {
    }

    buildRepository("languageX", RuleClass.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void class_without_sqale_annotation() throws Exception {
    @Rule(key = "key1", name = "name1", description = "description1")
    class RuleClass {
    }

    buildSingleRuleRepository(RuleClass.class);
  }

  @Test
  public void class_with_sqale_constant_remediation() throws Exception {

    @Rule(key = "key1", name = "name1", description = "description1")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("10min")
    class RuleClass {
    }

    RulesDefinition.Rule rule = buildSingleRuleRepository(RuleClass.class);
    assertThat(rule.debtSubCharacteristic()).isEqualTo(SubCharacteristics.CPU_EFFICIENCY);
    assertRemediation(rule, Type.CONSTANT_ISSUE, null, "10min", null);
  }

  @Test
  public void class_with_sqale_linear_remediation() throws Exception {

    @Rule(key = "key1", name = "name1", description = "description1")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleLinearRemediation(coeff = "2h", effortToFixDescription = "Effort to test one uncovered condition")
    class RuleClass {
    }

    RulesDefinition.Rule rule = buildSingleRuleRepository(RuleClass.class);
    assertRemediation(rule, Type.LINEAR, "2h", null, "Effort to test one uncovered condition");
  }

  @Test
  public void class_with_sqale_linear_with_offset_remediation() throws Exception {

    @Rule(key = "key1", name = "name1", description = "description1")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleLinearWithOffsetRemediation(coeff = "5min", offset = "1h",
      effortToFixDescription = "Effort to test one uncovered condition")
    class RuleClass {
    }

    RulesDefinition.Rule rule = buildSingleRuleRepository(RuleClass.class);
    assertRemediation(rule, Type.LINEAR_OFFSET, "5min", "1h", "Effort to test one uncovered condition");
  }

  @Test(expected = IllegalArgumentException.class)
  public void class_with_several_sqale_remediation_annotations() throws Exception {
    @Rule(key = "key1", name = "name1", description = "description1")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("10min")
    @SqaleLinearRemediation(coeff = "2h", effortToFixDescription = "Effort to test one uncovered condition")
    class RuleClass {
    }

    buildSingleRuleRepository(RuleClass.class);
  }

  @Test
  public void invalid_sqale_annotation() throws Exception {
    @Rule(key = "key1", name = "name1", description = "description1")
    @SqaleSubCharacteristic(SubCharacteristics.CPU_EFFICIENCY)
    @SqaleConstantRemediation("xxx")
    class MyInvalidRuleClass {
    }

    try {
      buildSingleRuleRepository(MyInvalidRuleClass.class);
      Assert.fail();
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("MyInvalidRuleClass");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void rule_not_created_by_RulesDefinitionAnnotationLoader() throws Exception {
    @Rule
    class RuleClass {
    }
    NewRepository newRepository = context.createRepository(REPO_KEY, "language1");
    AnnotationBasedRulesDefinition rulesDef = new AnnotationBasedRulesDefinition(newRepository, "language1");
    rulesDef.newRule(RuleClass.class);
  }

  private void assertRemediation(RulesDefinition.Rule rule, Type type, String coeff, String offset, String effortDesc) {
    DebtRemediationFunction remediationFunction = rule.debtRemediationFunction();
    assertThat(remediationFunction.type()).isEqualTo(type);
    assertThat(remediationFunction.coefficient()).isEqualTo(coeff);
    assertThat(remediationFunction.offset()).isEqualTo(offset);
    assertThat(rule.effortToFixDescription());
  }

  private void assertParam(Param param, String expectedKey, String expectedDescription) {
    assertThat(param.key()).isEqualTo(expectedKey);
    assertThat(param.name()).isEqualTo(expectedKey);
    assertThat(param.description()).isEqualTo(expectedDescription);
  }

  private RulesDefinition.Rule buildSingleRuleRepository(Class<?> ruleClass) {
    Repository repository = buildRepository(ruleClass);
    assertThat(repository.rules()).hasSize(1);
    return repository.rules().get(0);
  }

  private Repository buildRepository(Class<?>... classes) {
    return buildRepository(LANGUAGE_KEY_WITH_RESOURCE_BUNDLE, classes);
  }

  private Repository buildRepository(String languageKey, Class<?>... classes) {
    NewRepository newRepository = context.createRepository(REPO_KEY, languageKey);
    AnnotationBasedRulesDefinition.load(newRepository, languageKey, ImmutableList.copyOf(classes));
    newRepository.done();
    return context.repository(REPO_KEY);
  }

}
