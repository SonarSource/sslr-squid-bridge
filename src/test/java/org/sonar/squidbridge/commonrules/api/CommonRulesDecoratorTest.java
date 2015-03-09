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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rule.RuleKey;
import org.sonar.squidbridge.commonrules.internal.CommonRulesConstants;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public final class CommonRulesDecoratorTest {

  private static final String REPO_KEY = CommonRulesConstants.REPO_KEY_PREFIX + "java";

  Resource resource = mock(Resource.class);
  DecoratorContext context = mock(DecoratorContext.class);
  DefaultFileSystem fs;

  @Before
  public void prepare() {
    fs = new DefaultFileSystem();
  }

  @Test
  public void test_metadata() throws Exception {
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), mock(ResourcePerspectives.class)) {
    };
    assertThat(decorator.toString()).isEqualTo("Common Rules for java");
    assertThat(decorator.language()).isEqualTo("java");
  }

  @Test
  public void verifyDependsUponMetrics() throws Exception {
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), mock(ResourcePerspectives.class)) {
    };
    List<Metric> metrics = decorator.dependsUponMetrics();
    assertThat(metrics.size()).isEqualTo(2);
    assertThat(metrics).containsOnly(CoreMetrics.LINE_COVERAGE, CoreMetrics.COMMENT_LINES_DENSITY);
  }

  @Test
  public void do_not_execute_if_no_source_files() {
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), mock(ResourcePerspectives.class)) {
    };
    assertThat(decorator.shouldExecuteOnProject(null)).isFalse();
  }

  @Test
  public void do_execute_if_source_files_and_active_rules() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));

    ActiveRules activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_DUPLICATED_BLOCKS))
      .activate()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_INSUFFICIENT_LINE_COVERAGE))
      .activate()
      .build();
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), mock(ResourcePerspectives.class)) {
    };

    assertThat(decorator.shouldExecuteOnProject(null)).isTrue();
  }

  @Test
  public void do_not_execute_if_no_active_rules() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));
    // Q profile is empty
    ActiveRules activeRules = new ActiveRulesBuilder().build();
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), mock(ResourcePerspectives.class)) {
    };
    assertThat(decorator.shouldExecuteOnProject(null)).isFalse();
  }

  @Test
  public void create_issue() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(resource.getLanguage()).thenReturn(new Java());
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 2.0));

    ActiveRules activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_DUPLICATED_BLOCKS))
      .activate()
      .build();
    ResourcePerspectives resourcePerspectives = mock(ResourcePerspectives.class);
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), resourcePerspectives) {
    };

    // ugly, this method initializes the decorator
    decorator.shouldExecuteOnProject(null);
    decorator.decorate(resource, context);

    verify(resourcePerspectives, times(1)).as(Issuable.class, resource);
  }

  @Test
  public void do_not_decorate_other_languages() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(resource.getLanguage()).thenReturn(new Php());
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 2.0));

    ActiveRules activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_DUPLICATED_BLOCKS))
      .activate()
      .build();
    ResourcePerspectives resourcePerspectives = mock(ResourcePerspectives.class);
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), resourcePerspectives) {
    };

    // ugly, this method initializes the decorator
    decorator.shouldExecuteOnProject(null);
    decorator.decorate(resource, context);

    verifyZeroInteractions(context, resourcePerspectives);
  }

  @Test
  public void do_not_decorate_directories() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));
    when(resource.getScope()).thenReturn(Scopes.DIRECTORY);
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 2.0));

    ActiveRules activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_DUPLICATED_BLOCKS))
      .activate()
      .build();
    ResourcePerspectives resourcePerspectives = mock(ResourcePerspectives.class);
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), resourcePerspectives) {
    };

    // ugly, this method initializes the decorator
    decorator.shouldExecuteOnProject(null);
    decorator.decorate(resource, context);

    verifyZeroInteractions(context, resourcePerspectives);
  }

  @Test
  public void do_not_decorate_if_missing_file_language() {
    fs.add(new DefaultInputFile("src/foo/bar.java").setLanguage("java"));
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(resource.getLanguage()).thenReturn(null);
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 2.0));

    ActiveRules activeRules = new ActiveRulesBuilder()
      .create(RuleKey.of(REPO_KEY, CommonRulesConstants.RULE_DUPLICATED_BLOCKS))
      .activate()
      .build();
    ResourcePerspectives resourcePerspectives = mock(ResourcePerspectives.class);
    CommonRulesDecorator decorator = new CommonRulesDecorator("java", fs, new CheckFactory(activeRules), resourcePerspectives) {
    };

    // ugly, this method initializes the decorator
    decorator.shouldExecuteOnProject(null);
    decorator.decorate(resource, context);

    verifyZeroInteractions(context, resourcePerspectives);
  }

  static class Php implements Language {
    @Override
    public String getKey() {
      return "php";
    }

    @Override
    public String getName() {
      return "PHP";
    }

    @Override
    public String[] getFileSuffixes() {
      return new String[0];
    }
  }

  static class Java extends AbstractLanguage {

    public Java() {
      super("java");
    }

    @Override
    public String[] getFileSuffixes() {
      return new String[0];
    }
  }
}
