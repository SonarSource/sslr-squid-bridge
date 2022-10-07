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
package org.sonar.squidbridge.rules;

import org.junit.Test;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.debt.DebtRemediationFunction.Type;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.fest.assertions.Assertions.assertThat;

public class SqaleXmlLoaderTest {

  private static final String REPO_KEY = "repoKey";
  private static final String LANGUAGE_KEY = "languageKey";

  private RulesDefinition.Context context = new RulesDefinition.Context();
  private NewRepository repository = context.createRepository(REPO_KEY, LANGUAGE_KEY);

  @Test
  public void constant_remediation() throws Exception {
    repository.createRule("myRuleKey").setName("name").setHtmlDescription("desc");
    SqaleXmlLoader.load(repository, "/rules/sqale-constant.xml");
    Rule rule = buildRepository().rule("myRuleKey");
    assertRemediation(rule, Type.CONSTANT_ISSUE, null, "10min");
  }

  @Test
  public void linear_remediation() throws Exception {
    repository.createRule("myRuleKey").setName("name").setHtmlDescription("desc");
    SqaleXmlLoader.load(repository, "/rules/sqale-linear.xml");
    Rule rule = buildRepository().rule("myRuleKey");
    assertRemediation(rule, Type.LINEAR, "5min", null);
  }

  @Test
  public void linear_with_offset_remediation() throws Exception {
    repository.createRule("myRuleKey").setName("name").setHtmlDescription("desc");
    SqaleXmlLoader.load(repository, "/rules/sqale-linear-offset.xml");
    Rule rule = buildRepository().rule("myRuleKey");
    assertRemediation(rule, Type.LINEAR_OFFSET, "2min", "15min");
  }

  @Test
  public void unknown_remediation_function() throws Exception {
    repository.createRule("myRuleKey").setName("name").setHtmlDescription("desc");
    SqaleXmlLoader.load(repository, "/rules/sqale-unknown-function.xml");
    Rule rule = buildRepository().rule("myRuleKey");
    assertThat(rule.debtRemediationFunction()).isNull();
  }

  @Test
  public void should_ignore_sqale_model_for_rule_not_in_repository() throws Exception {
    SqaleXmlLoader.load(repository, "/rules/sqale-constant.xml");
    assertThat(buildRepository().rules()).isEmpty();
  }

  @Test(expected = IllegalStateException.class)
  public void empty_xml_file() throws Exception {
    SqaleXmlLoader.load(repository, "/rules/empty.xml");
  }

  private Repository buildRepository() {
    repository.done();
    return context.repository(REPO_KEY);
  }

  private void assertRemediation(RulesDefinition.Rule rule, Type type, String coeff, String offset) {
    DebtRemediationFunction remediationFunction = rule.debtRemediationFunction();
    assertThat(remediationFunction.type()).isEqualTo(type);
    assertThat(remediationFunction.gapMultiplier()).isEqualTo(coeff);
    assertThat(remediationFunction.baseEffort()).isEqualTo(offset);
  }

}
