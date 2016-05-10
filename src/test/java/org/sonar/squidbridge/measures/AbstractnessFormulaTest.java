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
import org.sonar.squidbridge.api.SourcePackage;

import static org.junit.Assert.assertEquals;

public class AbstractnessFormulaTest {

  AbstractnessFormula abstractness = new AbstractnessFormula();
  SourcePackage measurable = new SourcePackage("pac1");

  @Test
  public void testCalculate() {
    measurable.setMeasure(Metric.CLASSES, 10);
    measurable.setMeasure(Metric.INTERFACES, 1);
    measurable.setMeasure(Metric.ABSTRACT_CLASSES, 1);

    assertEquals(0.2, abstractness.calculate(measurable), 0);
  }

  @Test
  public void testCalculateOnEmptyProject() {
    measurable.setMeasure(Metric.CLASSES, 0);
    measurable.setMeasure(Metric.INTERFACES, 0);
    measurable.setMeasure(Metric.ABSTRACT_CLASSES, 0);

    assertEquals(0, abstractness.calculate(measurable), 0);
  }

}
