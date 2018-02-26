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
package org.sonar.squidbridge.metrics;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

import org.sonar.squidbridge.test.miniC.MiniCAstScanner.MiniCMetrics;

import org.sonar.squidbridge.api.SourceFile;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

public class ComplexityVisitorTest {

  @Test
  public void counter() {
    SourceFile sourceFile = scanFile("/metrics/complexity.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMPLEXITY)).isEqualTo(4);
    assertThat(sourceFile.getInt(MiniCMetrics.STATEMENTS)).isEqualTo(11);
    assertThat(sourceFile.getInt(MiniCMetrics.FUNCTIONS)).isEqualTo(2);
  }

}
