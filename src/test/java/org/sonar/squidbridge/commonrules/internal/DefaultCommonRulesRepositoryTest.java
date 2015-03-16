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
package org.sonar.squidbridge.commonrules.internal;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.server.rule.RulesDefinition.Repository;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class DefaultCommonRulesRepositoryTest {

  DefaultCommonRulesRepository repository = new DefaultCommonRulesRepository("java");

  @Test
  public void validate_all_rules() {
    repository
      .enableFailedUnitTestsRule()
      .enableDuplicatedBlocksRule()
      .enableInsufficientCommentDensityRule(42.0)
      .enableInsufficientBranchCoverageRule(42.0)
      .enableInsufficientLineCoverageRule(42.0)
      .enableSkippedUnitTestsRule();

    RulesDefinition.Context context = new RulesDefinition.Context();
    repository.define(context);

    assertThat(context.repository("common-java").rules()).hasSize(CommonRulesConstants.CLASSES.size());
  }

  @Test
  public void test_metadata() throws Exception {
    RulesDefinition.Context context = new RulesDefinition.Context();
    repository.define(context);

    assertThat(context.repository("common-java").key()).isEqualTo(CommonRulesConstants.REPO_KEY_PREFIX + "java");
    assertThat(context.repository("common-java").name()).isEqualTo("Common SonarQube");
    assertThat(context.repository("common-java").language()).isEqualTo("java");
  }

  @Test
  public void all_rules_are_disabled() {
    RulesDefinition.Context context = new RulesDefinition.Context();
    repository.define(context);

    assertThat(context.repository("common-java").rules()).isEmpty();
    assertThat(repository.enabledRuleKeys()).isEmpty();
  }

  @Test
  public void enable_rule_with_default_param_value() {
    repository.enableInsufficientCommentDensityRule(null);

    RulesDefinition.Context context = new RulesDefinition.Context();
    repository.define(context);

    Repository repo = context.repository("common-java");
    assertThat(repo.rules()).hasSize(1);
    org.sonar.api.server.rule.RulesDefinition.Rule rule = repo.rule(CommonRulesConstants.RULE_INSUFFICIENT_COMMENT_DENSITY);
    assertThat(rule).isNotNull();
    assertThat(Double.parseDouble(rule.param(CommonRulesConstants.PARAM_MIN_COMMENT_DENSITY).defaultValue())).isEqualTo(25.0);
    assertThat(repository.enabledRuleKeys()).containsOnly(CommonRulesConstants.RULE_INSUFFICIENT_COMMENT_DENSITY);
  }

  @Test
  public void override_default_param_value() {
    repository.enableInsufficientCommentDensityRule(42.0);

    RulesDefinition.Context context = new RulesDefinition.Context();
    repository.define(context);

    Repository repo = context.repository("common-java");
    assertThat(repo.rules()).hasSize(1);
    org.sonar.api.server.rule.RulesDefinition.Rule rule = repo.rule(CommonRulesConstants.RULE_INSUFFICIENT_COMMENT_DENSITY);
    assertThat(rule).isNotNull();
    assertThat(Double.parseDouble(rule.param(CommonRulesConstants.PARAM_MIN_COMMENT_DENSITY).defaultValue())).isEqualTo(42.0);
  }

  @Test(expected = IllegalStateException.class)
  public void invalid_param() throws Exception {
    DefaultCommonRulesRepository.ruleParam(mock(NewRule.class), "xxx");
  }

}
