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
  key = "FailedUnitTests",
  name = "Failed unit tests",
  priority = Priority.MAJOR,
  tags = "bug",
  description = "<p>Test failures or errors generally indicate that regressions have been introduced. " +
    "Those tests should be handled as soon as possible to reduce the cost to fix the corresponding regressions.</p>")
@SqaleSubCharacteristic(SubCharacteristics.UNIT_TESTABILITY)
@SqaleLinearRemediation(coeff = "10min", effortToFixDescription = "number of failed tests")
public class FailedUnitTestsCheck extends CommonCheck {

  @Override
  public void checkResource(Resource resource, DecoratorContext context, RuleKey ruleKey, ResourcePerspectives perspectives) {
    double testErrors = MeasureUtils.getValue(context.getMeasure(CoreMetrics.TEST_ERRORS), 0.0);
    double testFailures = MeasureUtils.getValue(context.getMeasure(CoreMetrics.TEST_FAILURES), 0.0);
    double testFailuresAndErrors = testErrors + testFailures;
    if (ResourceUtils.isUnitTestClass(resource) && testFailuresAndErrors > 0) {
      createIssue(resource, ruleKey, testFailuresAndErrors, perspectives);
    }
  }

  private void createIssue(Resource resource, RuleKey ruleKey, double testFailuresAndErrors, ResourcePerspectives perspectives) {
    createIssue(resource, perspectives, ruleKey, testFailuresAndErrors, "Some tests are not successful. You should fix them.");
  }

}
