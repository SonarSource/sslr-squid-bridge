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
package org.sonar.squidbridge.api;

import java.util.HashSet;
import java.util.Set;

public class SourceFile extends SourceCode {

  private final Set<Integer> noSonarTagLines = new HashSet<Integer>();

  public SourceFile(String key) {
    super(key);
    setStartAtLine(1);
  }

  public SourceFile(String key, String fileName) {
    super(key, fileName);
    setStartAtLine(1);
  }

  public Set<Integer> getNoSonarTagLines() {
    return noSonarTagLines;
  }

  public boolean hasNoSonarTagAtLine(int lineNumber) {
    return noSonarTagLines.contains(lineNumber);
  }

  public void addNoSonarTagLines(Set<Integer> noSonarTagLines) {
    this.noSonarTagLines.addAll(noSonarTagLines);
  }

  public void addNoSonarTagLine(int line) {
    noSonarTagLines.add(line);
  }

}
