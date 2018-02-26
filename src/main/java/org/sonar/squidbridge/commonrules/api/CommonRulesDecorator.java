/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2018 SonarSource SA
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
package org.sonar.squidbridge.commonrules.api;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorBarriers;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.squidbridge.commonrules.internal.CommonRulesConstants;
import org.sonar.squidbridge.commonrules.internal.DefaultCommonRulesRepository;
import org.sonar.squidbridge.commonrules.internal.checks.CommonCheck;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated common rules are integrated to SonarQube 5.2. It's transparent for plugins that use this Decorator, so
 * that they can support SQ 4.5 LTS and 5.x at the same time.
 */
@Deprecated
@DependedUpon(value = DecoratorBarriers.ISSUES_ADDED)
public abstract class CommonRulesDecorator implements Decorator {

  private final FileSystem fs;
  private final String language;
  private final CheckFactory checkFactory;
  private final ResourcePerspectives perspectives;
  private Checks<CommonCheck> checks;

  public CommonRulesDecorator(String language, FileSystem fs, CheckFactory checkFactory, ResourcePerspectives perspectives) {
    this.language = language;
    this.fs = fs;
    this.checkFactory = checkFactory;
    this.perspectives = perspectives;
  }

  public String language() {
    return language;
  }

  @DependsUpon
  public List<Metric> dependsUponMetrics() {
    return Arrays.<Metric>asList(CoreMetrics.LINE_COVERAGE, CoreMetrics.COMMENT_LINES_DENSITY);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    if (!fs.hasFiles(fs.predicates().hasLanguage(language))) {
      return false;
    }
    checks = checkFactory.create(DefaultCommonRulesRepository.keyForLanguage(language));
    checks.addAnnotatedChecks(CommonRulesConstants.CLASSES);
    return !checks.all().isEmpty();
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    Language resourceLanguage = resource.getLanguage();
    // assume that all checks relate to files, not directories nor modules
    if (ResourceUtils.isEntity(resource) && resourceLanguage != null && resourceLanguage.getKey().equals(language)) {
      for (CommonCheck check : checks.all()) {
        check.checkResource(resource, context, checks.ruleKey(check), perspectives);
      }
    }
  }

  @Override
  public String toString() {
    return "Common Rules for " + language;
  }
}
