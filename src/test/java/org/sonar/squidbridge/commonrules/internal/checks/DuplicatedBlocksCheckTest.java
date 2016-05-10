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
package org.sonar.squidbridge.commonrules.internal.checks;

import org.sonar.squidbridge.commonrules.internal.checks.DuplicatedBlocksCheck;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DuplicatedBlocksCheckTest {

  DuplicatedBlocksCheck check = new DuplicatedBlocksCheck();
  Resource resource = mock(Resource.class);
  DecoratorContext context = mock(DecoratorContext.class);
  private ResourcePerspectives perspectives;

  @Before
  public void before() {
    perspectives = mock(ResourcePerspectives.class);
  }

  @Test
  public void checkShouldGenerateViolationOnFileWithDuplicatedBlocks() {
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 2.0));

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(1)).as(Issuable.class, resource);
  }

  @Test
  public void checkShouldNotGenerateViolationOnFileWithoutDuplicatedBlocks() {
    when(resource.getScope()).thenReturn(Scopes.FILE);
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 0.0));

    check.checkResource(resource, context, null, perspectives);

    verify(perspectives, times(0)).as(Issuable.class, resource);
  }

}
