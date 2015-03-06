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

import javax.annotation.Nullable;

/**
 * This is not an extension point. Implementation is provided by the library.
 */
public interface CommonRulesRepository {

  String RULE_INSUFFICIENT_BRANCH_COVERAGE = "InsufficientBranchCoverage";
  String PARAM_MIN_BRANCH_COVERAGE = "minimumBranchCoverageRatio";

  String RULE_INSUFFICIENT_LINE_COVERAGE = "InsufficientLineCoverage";
  String PARAM_MIN_LINE_COVERAGE = "minimumLineCoverageRatio";

  String RULE_INSUFFICIENT_COMMENT_DENSITY = "InsufficientCommentDensity";
  String PARAM_MIN_COMMENT_DENSITY = "minimumCommentDensity";

  String RULE_DUPLICATED_BLOCKS = "DuplicatedBlocks";
  String RULE_SKIPPED_UNIT_TESTS = "SkippedUnitTests";
  String RULE_FAILED_UNIT_TESTS = "FailedUnitTests";

  CommonRulesRepository enableInsufficientBranchCoverageRule(@Nullable Double minBranchCoverageRatio);

  CommonRulesRepository enableInsufficientLineCoverageRule(@Nullable Double minLineCoverageRatio);

  CommonRulesRepository enableInsufficientCommentDensityRule(@Nullable Double minCommentDensity);

  CommonRulesRepository enableDuplicatedBlocksRule();

  CommonRulesRepository enableSkippedUnitTestsRule();

  CommonRulesRepository enableFailedUnitTestsRule();
}
