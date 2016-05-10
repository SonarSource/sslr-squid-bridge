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
package org.sonar.squidbridge.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StringArrayReader extends Reader {

  private final StringReader stringReader;

  enum EndOfLineDelimiter {
    LF, CR_PLUS_LF, CR
  }

  public StringArrayReader(String[] lines) {
    this(lines, EndOfLineDelimiter.LF);
  }

  public StringArrayReader(String[] lines, EndOfLineDelimiter endOfLineDelimiter) {
    if (lines == null) {
      throw new IllegalStateException("lines object can't be null.");
    }
    String content = convertArrayToStringAndAppendEndOfLine(lines, endOfLineDelimiter);
    stringReader = new StringReader(content);
  }

  private String convertArrayToStringAndAppendEndOfLine(String[] lines, EndOfLineDelimiter endOfLineDelimiter) {
    StringBuilder content = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      content.append(lines[i]);
      if (i != lines.length - 1) {
        switch (endOfLineDelimiter) {
          case LF:
            content.append('\n');
            break;
          case CR:
            content.append('\r');
            break;
          case CR_PLUS_LF:
            content.append("\r\n");
            break;
          default:
            // should never happen
            throw new IllegalStateException();
        }
      }
    }
    return content.toString();
  }

  @Override
  public void close() throws IOException {
    stringReader.close();
  }

  @Override
  public boolean ready() throws IOException {
    return stringReader.ready();
  }

  @Override
  public boolean markSupported() {
    return stringReader.markSupported();
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    stringReader.mark(readAheadLimit);
  }

  @Override
  public void reset() throws IOException {
    stringReader.reset();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    return stringReader.read(cbuf, off, len);
  }
}
