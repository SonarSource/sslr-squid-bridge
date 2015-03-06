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

import org.sonar.squidbridge.commonrules.internal.checks.BranchCoverageCheck;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BranchCoverageCheckTest {

  BranchCoverageCheck check = new BranchCoverageCheck();
  Resource resource = mock(Resource.class);
  DecoratorContext context = mock(DecoratorContext.class);
  private ResourcePerspectives perspectives;

  @Before
  public void before() {
    perspectives = mock(ResourcePerspectives.class);
  }

  @Test
  public void checkShouldNotGenerateViolationOnFileWithGoodLineCoverage() {
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(context.getMeasure(CoreMetrics.BRANCH_COVERAGE)).thenReturn(new Measure(CoreMetrics.BRANCH_COVERAGE, 85.0));

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(0)).as(Issuable.class, resource);
  }

  @Test
  public void checkShouldNotGenerateViolationOnFileWithoutLineCoverage() {
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(context.getMeasure(CoreMetrics.BRANCH_COVERAGE)).thenReturn(null);

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(0)).as(Issuable.class, resource);
  }

  @Test
  public void checkShoulGenerateViolationOnFileWithBadLineCoverage() {
    check.setMinimumBranchCoverageRatio(60);
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(context.getMeasure(CoreMetrics.BRANCH_COVERAGE)).thenReturn(new Measure(CoreMetrics.BRANCH_COVERAGE, 20.0));
    when(context.getMeasure(CoreMetrics.CONDITIONS_TO_COVER)).thenReturn(new Measure(CoreMetrics.CONDITIONS_TO_COVER, 99.9));
    when(context.getMeasure(CoreMetrics.UNCOVERED_CONDITIONS)).thenReturn(new Measure(CoreMetrics.UNCOVERED_CONDITIONS, 80.0));

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(1)).as(Issuable.class, resource);
  }

  @Test
  public void test_toString() {
    assertThat(check.toString()).isEqualTo("BranchCoverageCheck[minimumBranchCoverageRatio=65.0]");

  }
}
