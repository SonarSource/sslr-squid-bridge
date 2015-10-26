/*
 * SSLR Squid Bridge
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
package org.sonar.squidbridge.measures;


public class CommentLinesDensityFormula implements CalculatedMetricFormula {

  @Override
  public double calculate(Measurable measurable) {
    double total = measurable.getDouble(Metric.LINES_OF_CODE) + measurable.getDouble(Metric.COMMENT_LINES_WITHOUT_HEADER);
    if (Double.doubleToRawLongBits(total) != 0) {
      return measurable.getDouble(Metric.COMMENT_LINES_WITHOUT_HEADER) / total;
    }
    return 0;
  }

}
