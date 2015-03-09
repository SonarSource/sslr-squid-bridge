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
package org.sonar.squidbridge.commonrules.api;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.squidbridge.commonrules.internal.CommonRulesConstants;
import org.sonar.squidbridge.commonrules.internal.DefaultCommonRulesRepository;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public final class CommonRulesEngineTest {

  static class JavaCommonRulesEngine extends CommonRulesEngine {
    public JavaCommonRulesEngine() {
      super("java");
    }

    @Override
    protected void doEnableRules(CommonRulesRepository repository) {
      repository.enableDuplicatedBlocksRule();

      // default value
      repository.enableInsufficientBranchCoverageRule(null);

      // override default value
      repository.enableInsufficientLineCoverageRule(82.0);
    }
  }

  @Test
  public void test_language() throws Exception {
    assertThat(new JavaCommonRulesEngine().language()).isEqualTo("java");
  }

  @Test
  public void enable_rules() throws Exception {
    JavaCommonRulesEngine engine = new JavaCommonRulesEngine();
    DefaultCommonRulesRepository repo = (DefaultCommonRulesRepository) engine.newRepository();

    RulesDefinition.Context context = new RulesDefinition.Context();
    repo.define(context);

    assertThat(context.repositories()).hasSize(1);
    Repository repository = context.repository("common-java");

    assertThat(repository.rules()).hasSize(3);
    assertThat(repository.rule(CommonRulesConstants.RULE_DUPLICATED_BLOCKS)).isNotNull();
    assertThat(repository.rule(CommonRulesConstants.RULE_INSUFFICIENT_COMMENT_DENSITY)).isNull();

    // hardcoded default value
    org.sonar.api.server.rule.RulesDefinition.Rule branchCoverage = repository.rule(CommonRulesConstants.RULE_INSUFFICIENT_BRANCH_COVERAGE);
    assertThat(branchCoverage).isNotNull();
    assertThat(Double.parseDouble(branchCoverage.param(CommonRulesConstants.PARAM_MIN_BRANCH_COVERAGE).defaultValue())).isEqualTo(65.0);

    org.sonar.api.server.rule.RulesDefinition.Rule lineCoverage = repository.rule(CommonRulesConstants.RULE_INSUFFICIENT_LINE_COVERAGE);
    assertThat(lineCoverage).isNotNull();
    assertThat(Double.parseDouble(lineCoverage.param(CommonRulesConstants.PARAM_MIN_LINE_COVERAGE).defaultValue())).isEqualTo(82.0);
  }

  @Test
  public void provide_rule_definitions() throws Exception {
    JavaCommonRulesEngine engine = new JavaCommonRulesEngine();
    List extensions = engine.provide();

    assertThat(extensions).hasSize(1);
    assertThat(extensions.get(0)).isInstanceOf(CommonRulesRepository.class);
  }
}
