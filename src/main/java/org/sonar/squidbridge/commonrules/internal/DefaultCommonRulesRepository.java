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

import com.google.common.annotations.VisibleForTesting;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;
import org.sonar.squidbridge.commonrules.api.CommonRulesRepository;
import org.sonar.squidbridge.commonrules.internal.checks.BranchCoverageCheck;
import org.sonar.squidbridge.commonrules.internal.checks.CommentDensityCheck;
import org.sonar.squidbridge.commonrules.internal.checks.DuplicatedBlocksCheck;
import org.sonar.squidbridge.commonrules.internal.checks.FailedUnitTestsCheck;
import org.sonar.squidbridge.commonrules.internal.checks.LineCoverageCheck;
import org.sonar.squidbridge.commonrules.internal.checks.SkippedUnitTestsCheck;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.PARAM_MIN_BRANCH_COVERAGE;
import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.PARAM_MIN_COMMENT_DENSITY;
import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.PARAM_MIN_LINE_COVERAGE;
import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.RULE_INSUFFICIENT_BRANCH_COVERAGE;
import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.RULE_INSUFFICIENT_COMMENT_DENSITY;
import static org.sonar.squidbridge.commonrules.internal.CommonRulesConstants.RULE_INSUFFICIENT_LINE_COVERAGE;

public class DefaultCommonRulesRepository implements RulesDefinition, CommonRulesRepository {

  private List<Class> enabledChecks = new ArrayList<Class>();
  private String language;
  private Double minimumBranchCoverageRatio;
  private Double minimumLineCoverageRatio;
  private Double minimumCommentDensity;

  public DefaultCommonRulesRepository(String language) {
    this.language = language;
  }

  @Override
  public void define(Context context) {
    NewRepository repo = context.createRepository(keyForLanguage(language), language)
      .setName("Common SonarQube");
    AnnotationBasedRulesDefinition.load(repo, language, enabledChecks);
    setParamValue(repo.rule(RULE_INSUFFICIENT_BRANCH_COVERAGE), PARAM_MIN_BRANCH_COVERAGE, minimumBranchCoverageRatio);
    setParamValue(repo.rule(RULE_INSUFFICIENT_LINE_COVERAGE), PARAM_MIN_LINE_COVERAGE, minimumLineCoverageRatio);
    setParamValue(repo.rule(RULE_INSUFFICIENT_COMMENT_DENSITY), PARAM_MIN_COMMENT_DENSITY, minimumCommentDensity);
    repo.done();
  }

  private void setParamValue(@Nullable NewRule rule, String paramName, @Nullable Double paramValue) {
    if (rule != null && paramValue != null) {
      ruleParam(rule, paramName).setDefaultValue("" + paramValue);
    }
  }

  @VisibleForTesting
  protected static NewParam ruleParam(NewRule rule, String paramName) {
    NewParam param = rule.param(paramName);
    if (param == null) {
      throw new IllegalStateException(paramName + " should be a valid parameter name on " + rule);
    }
    return param;
  }

  public static String keyForLanguage(String language) {
    return CommonRulesConstants.REPO_KEY_PREFIX + language;
  }

  @Override
  public DefaultCommonRulesRepository enableInsufficientBranchCoverageRule(@Nullable Double minimumBranchCoverageRatio) {
    this.minimumBranchCoverageRatio = minimumBranchCoverageRatio;
    enabledChecks.add(BranchCoverageCheck.class);
    return this;
  }

  @Override
  public DefaultCommonRulesRepository enableInsufficientLineCoverageRule(@Nullable Double minimumLineCoverageRatio) {
    this.minimumLineCoverageRatio = minimumLineCoverageRatio;
    enabledChecks.add(LineCoverageCheck.class);
    return this;
  }

  @Override
  public DefaultCommonRulesRepository enableInsufficientCommentDensityRule(@Nullable Double minimumCommentDensity) {
    this.minimumCommentDensity = minimumCommentDensity;
    enabledChecks.add(CommentDensityCheck.class);
    return this;
  }

  @Override
  public DefaultCommonRulesRepository enableDuplicatedBlocksRule() {
    enabledChecks.add(DuplicatedBlocksCheck.class);
    return this;
  }

  @Override
  public DefaultCommonRulesRepository enableSkippedUnitTestsRule() {
    enabledChecks.add(SkippedUnitTestsCheck.class);
    return this;
  }

  @Override
  public DefaultCommonRulesRepository enableFailedUnitTestsRule() {
    enabledChecks.add(FailedUnitTestsCheck.class);
    return this;
  }
}
