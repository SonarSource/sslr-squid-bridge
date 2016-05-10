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
package org.sonar.squidbridge.commonrules.internal;

import org.sonar.squidbridge.commonrules.internal.checks.BranchCoverageCheck;
import org.sonar.squidbridge.commonrules.internal.checks.CommentDensityCheck;
import org.sonar.squidbridge.commonrules.internal.checks.DuplicatedBlocksCheck;
import org.sonar.squidbridge.commonrules.internal.checks.FailedUnitTestsCheck;
import org.sonar.squidbridge.commonrules.internal.checks.LineCoverageCheck;
import org.sonar.squidbridge.commonrules.internal.checks.SkippedUnitTestsCheck;

import java.util.Arrays;
import java.util.List;

public class CommonRulesConstants {

  /**
   * The prefix used to create the rule repository for a given language (using its key).
   * For instance : "common-java".
   */
  public static final String REPO_KEY_PREFIX = "common-";

  public static final String RULE_INSUFFICIENT_BRANCH_COVERAGE = "InsufficientBranchCoverage";
  public static final String PARAM_MIN_BRANCH_COVERAGE = "minimumBranchCoverageRatio";

  public static final String RULE_INSUFFICIENT_LINE_COVERAGE = "InsufficientLineCoverage";
  public static final String PARAM_MIN_LINE_COVERAGE = "minimumLineCoverageRatio";

  public static final String RULE_INSUFFICIENT_COMMENT_DENSITY = "InsufficientCommentDensity";
  public static final String PARAM_MIN_COMMENT_DENSITY = "minimumCommentDensity";

  public static final String RULE_DUPLICATED_BLOCKS = "DuplicatedBlocks";
  public static final String RULE_SKIPPED_UNIT_TESTS = "SkippedUnitTests";
  public static final String RULE_FAILED_UNIT_TESTS = "FailedUnitTests";

  /**
   * List of existing checks.
   */
  public static final List<Class> CLASSES = Arrays.<Class>asList(
    DuplicatedBlocksCheck.class,
    LineCoverageCheck.class,
    BranchCoverageCheck.class,
    CommentDensityCheck.class,
    SkippedUnitTestsCheck.class,
    FailedUnitTestsCheck.class);

  private CommonRulesConstants() {
    // This class should not be instantiated.
  }
}
