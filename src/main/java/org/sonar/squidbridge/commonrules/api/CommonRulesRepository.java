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
package org.sonar.squidbridge.commonrules.api;

import javax.annotation.Nullable;

import java.util.Set;

/**
 * This is not an extension point. Implementation is provided by the library.
 * @deprecated common rules are integrated to SonarQube 5.2. It's transparent for plugins that use this Decorator, so
 * that they can support SQ 4.5 LTS and 5.x at the same time.
 */
@Deprecated
public interface CommonRulesRepository {

  CommonRulesRepository enableInsufficientBranchCoverageRule(@Nullable Double minBranchCoverageRatio);

  CommonRulesRepository enableInsufficientLineCoverageRule(@Nullable Double minLineCoverageRatio);

  CommonRulesRepository enableInsufficientCommentDensityRule(@Nullable Double minCommentDensity);

  CommonRulesRepository enableDuplicatedBlocksRule();

  CommonRulesRepository enableSkippedUnitTestsRule();

  CommonRulesRepository enableFailedUnitTestsRule();

  /**
   * Used for unit-testing
   */
  Set<String> enabledRuleKeys();
}
