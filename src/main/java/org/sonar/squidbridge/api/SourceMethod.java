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
package org.sonar.squidbridge.api;

import org.sonar.squidbridge.measures.Metric;

public class SourceMethod extends SourceCode {

  /**
   * This is used only for Java for now, but can be used for other languages. So maybe we should push it down to SourceCode.
   */
  private boolean suppressWarnings = false;

  public SourceMethod(String key) {
    super(key);
  }

  public SourceMethod(SourceClass peekParentClass, String methodSignature, int startAtLine) {
    super(peekParentClass.getKey() + "#" + methodSignature, methodSignature);
    setStartAtLine(startAtLine);
  }

  public boolean isAccessor() {
    return getInt(Metric.ACCESSORS) != 0;
  }

  public void setSuppressWarnings(boolean suppressWarnings) {
    this.suppressWarnings = suppressWarnings;
  }

  public boolean isSuppressWarnings() {
    return suppressWarnings;
  }
}
