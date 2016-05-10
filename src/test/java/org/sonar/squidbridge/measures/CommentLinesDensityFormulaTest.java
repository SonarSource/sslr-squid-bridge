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
package org.sonar.squidbridge.measures;

import org.junit.Test;
import org.sonar.squidbridge.api.SourceClass;

import static org.junit.Assert.assertEquals;

public class CommentLinesDensityFormulaTest {

  CommentLinesDensityFormula formula = new CommentLinesDensityFormula();
  SourceClass measurable = new SourceClass("com.Toto");

  @Test
  public void calculateDensityOnEmptyFile() {
    measurable.setMeasure(Metric.LINES_OF_CODE, 0);
    measurable.setMeasure(Metric.COMMENT_LINES, 0);
    assertEquals(0, measurable.getDouble(Metric.COMMENT_LINES_DENSITY), 0.01);
  }

  @Test
  public void calculate() {
    measurable.setMeasure(Metric.LINES_OF_CODE, 10);
    measurable.setMeasure(Metric.COMMENT_LINES, 10);
    assertEquals(0.5, measurable.getDouble(Metric.COMMENT_LINES_DENSITY), 0.01);
  }

}
