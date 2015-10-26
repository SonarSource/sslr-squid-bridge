/*
 * SSLR Squid Bridge
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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

@Rule(
  key = "InsufficientCommentDensity",
  name = "Source files should have a sufficient density of comment lines",
  priority = Priority.MAJOR,
  tags = "convention",
  description = "<p>An issue is created on a file as soon as the density of comment lines on this file is less than the required threshold. "
    + "The number of comment lines to be written in order to reach the required threshold is provided by each issue message.</p>")
@SqaleSubCharacteristic(SubCharacteristics.UNDERSTANDABILITY)
@SqaleLinearRemediation(coeff = "2min", effortToFixDescription = "number of lines required to meet minimum density")
public class CommentDensityCheck extends CommonCheck {

  private static final double DEFAULT_MIN_DENSITY = 25;

  @RuleProperty(key = "minimumCommentDensity", description = "The minimum required comment density.", defaultValue = "" + DEFAULT_MIN_DENSITY)
  private double minimumCommentDensity = DEFAULT_MIN_DENSITY;

  @Override
  public void checkResource(Resource resource, DecoratorContext context, RuleKey rule, ResourcePerspectives perspectives) {
    if (minimumCommentDensity < 0 || minimumCommentDensity >= 100) {
      throw new IllegalArgumentException(minimumCommentDensity
        + " is not a valid value for minimum required comment density for rule 'CommentDensityCheck' (must be >= 0 and < 100).");
    }

    double commentDensity = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COMMENT_LINES_DENSITY), 0.0);
    double linesOfCode = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    if (commentDensity < minimumCommentDensity && Double.doubleToRawLongBits(linesOfCode) != 0L) {
      double commentLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COMMENT_LINES), 0.0);
      double missingCommentLines = Math.ceil(minimumCommentDensity * linesOfCode / (100 - minimumCommentDensity) - commentLines);

      createIssue(resource, rule, missingCommentLines, perspectives);
    }
  }

  private void createIssue(Resource resource, RuleKey ruleKey, double missingCommentLines, ResourcePerspectives perspectives) {
    createIssue(resource, perspectives, ruleKey, missingCommentLines, (int) missingCommentLines + " more comment lines need to be written to reach the minimum threshold of "
      + minimumCommentDensity + "% comment density.");
  }

  public void setMinimumCommentDensity(int threshold) {
    this.minimumCommentDensity = threshold;
  }
}
