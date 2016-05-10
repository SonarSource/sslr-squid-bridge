/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.squidbridge.commonrules.internal.checks;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition.SubCharacteristics;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.SqaleLinearRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "SkippedUnitTests",
  name = "Skipped unit tests should be either removed or fixed",
  priority = Priority.MAJOR,
  tags = "pitfall",
  description = "<p>Skipped unit tests are considered as dead code. " +
    "Either they should be activated again (and updated) or they should be removed.</p>")
@SqaleSubCharacteristic(SubCharacteristics.UNIT_TESTABILITY)
@SqaleLinearRemediation(coeff = "10min", effortToFixDescription = "number of skipped tests")
public class SkippedUnitTestsCheck extends CommonCheck {

  @Override
  public void checkResource(Resource resource, DecoratorContext context, RuleKey ruleKey, ResourcePerspectives perspectives) {
    double skippedTests = MeasureUtils.getValue(context.getMeasure(CoreMetrics.SKIPPED_TESTS), 0.0);
    if (ResourceUtils.isUnitTestClass(resource) && skippedTests > 0) {
      createIssue(resource, ruleKey, skippedTests, perspectives);
    }
  }

  private void createIssue(Resource resource, RuleKey ruleKey, double skippedTests, ResourcePerspectives perspectives) {
    String fileName = resource.getName();
    createIssue(resource, perspectives, ruleKey, skippedTests, "Fix or remove skipped unit tests in file \"" + fileName + "\".");
  }

}
