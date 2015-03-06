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

import org.sonar.squidbridge.commonrules.internal.checks.SkippedUnitTestsCheck;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkippedUnitTestsCheckTest {

  private SkippedUnitTestsCheck check;
  private Resource resource;
  private DecoratorContext context;
  private ResourcePerspectives perspectives;

  @Before
  public void before() {
    check = new SkippedUnitTestsCheck();
    resource = mock(Resource.class);
    context = mock(DecoratorContext.class);
    perspectives = mock(ResourcePerspectives.class);
  }

  @Test
  public void checkShouldNotGenerateViolationIfNotTest() {
    when(resource.getQualifier()).thenReturn(Qualifiers.FILE);
    check.checkResource(resource, context, null, perspectives);
    verify(perspectives, times(0)).as(Issuable.class, resource);
  }

  @Test
  public void checkShouldNotGenerateViolationIfNoSkippedTests() {
    when(resource.getQualifier()).thenReturn(Qualifiers.UNIT_TEST_FILE);

    // this test has no "skipped_test" measure
    check.checkResource(resource, context, null, perspectives);
    verify(perspectives, times(0)).as(Issuable.class, resource);

    // this is the case of a test file that has no skipped test
    when(context.getMeasure(CoreMetrics.SKIPPED_TESTS)).thenReturn(new Measure(CoreMetrics.SKIPPED_TESTS, 0.0));
    check.checkResource(resource, context, null, mock(ResourcePerspectives.class));
    verify(perspectives, times(0)).as(Issuable.class, resource);
  }

  @Test
  public void checkShouldGenerateViolationOnFileWithSkippedTests() {
    when(resource.getQualifier()).thenReturn(Qualifiers.UNIT_TEST_FILE);
    when(context.getMeasure(CoreMetrics.SKIPPED_TESTS)).thenReturn(new Measure(CoreMetrics.SKIPPED_TESTS, 4.0));

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(1)).as(Issuable.class, resource);
  }

}
