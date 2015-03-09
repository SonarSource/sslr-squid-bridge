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
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition.SubCharacteristics;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.SqaleLinearRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.commonrules.api.CommonRulesRepository;

@Rule(
  key = CommonRulesRepository.RULE_INSUFFICIENT_BRANCH_COVERAGE,
  name = "Branches should have sufficient coverage by unit tests",
  priority = Priority.MAJOR,
  tags = "bad-practice",
  description = "<p>An issue is created on a file as soon as the branch coverage on this file is less than the required threshold."
    + "It gives the number of branches to be covered in order to reach the required threshold.</p>")
@SqaleSubCharacteristic(SubCharacteristics.UNIT_TESTABILITY)
@SqaleLinearRemediation(coeff = "5min", effortToFixDescription = "number of uncovered conditions")
public class BranchCoverageCheck extends CommonCheck {

  private static final double DEFAULT_RATIO = 65;

  @RuleProperty(key = CommonRulesRepository.PARAM_MIN_BRANCH_COVERAGE, description = "The minimum required branch coverage ratio.", defaultValue = "" + DEFAULT_RATIO)
  private double minimumBranchCoverageRatio = DEFAULT_RATIO;

  @Override
  public void checkResource(Resource resource, DecoratorContext context, RuleKey rule, ResourcePerspectives perspectives) {
    double lineCoverage = MeasureUtils.getValue(context.getMeasure(CoreMetrics.BRANCH_COVERAGE), 0.0);
    if (context.getMeasure(CoreMetrics.BRANCH_COVERAGE) != null && lineCoverage < minimumBranchCoverageRatio) {
      double uncoveredConditions = MeasureUtils.getValue(context.getMeasure(CoreMetrics.UNCOVERED_CONDITIONS), 0.0);
      double conditionsToCover = MeasureUtils.getValue(context.getMeasure(CoreMetrics.CONDITIONS_TO_COVER), 0.0);
      double conditionsToCoverToReachThreshold = Math.ceil((conditionsToCover * minimumBranchCoverageRatio / 100)
        - (conditionsToCover - uncoveredConditions));

      createIssue(resource, rule, conditionsToCoverToReachThreshold, perspectives);
    }
  }

  private void createIssue(Resource resource, RuleKey ruleKey, double conditionsToCoverToReachThreshold, ResourcePerspectives perspectives) {
    createIssue(resource, perspectives, ruleKey, conditionsToCoverToReachThreshold, (int) conditionsToCoverToReachThreshold
      + " more branches need to be covered by unit tests to reach the minimum threshold of " + minimumBranchCoverageRatio
      + "% branch coverage.");
  }

  public void setMinimumBranchCoverageRatio(int threshold) {
    this.minimumBranchCoverageRatio = threshold;
  }
}
