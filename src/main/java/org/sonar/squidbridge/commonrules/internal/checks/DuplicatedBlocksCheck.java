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
import org.sonar.check.Priority;
import org.sonar.check.Rule;

@Rule(
  key = "DuplicatedBlocks",
  name = "Duplicated blocks",
  priority = Priority.MAJOR,
  description = "<p>An issue is created on a file as soon as there is a block of duplicated code on this file. "
    + "It gives the number of blocks in the file.</p>")
public class DuplicatedBlocksCheck extends CommonCheck {

  @Override
  public void checkResource(Resource resource, DecoratorContext context, RuleKey rule, ResourcePerspectives perspectives) {
    double duplicatedBlocks = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS), 0.0);
    if (duplicatedBlocks > 0) {
      createIssue(resource, rule, duplicatedBlocks, perspectives);
    }
  }

  private void createIssue(Resource resource, RuleKey ruleKey, double duplicatedBlocks, ResourcePerspectives perspectives) {
    createIssue(resource, perspectives, ruleKey, duplicatedBlocks, (int) duplicatedBlocks + " duplicated blocks of code.");
  }
}
