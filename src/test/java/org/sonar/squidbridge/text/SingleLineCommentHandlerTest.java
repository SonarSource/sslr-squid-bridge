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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SingleLineCommentHandlerTest {

  @Test(expected = IllegalStateException.class)
  public void illegalCallToMatchToEnd() {
    SingleLineCommentHandler handler = new SingleLineCommentHandler("//");
    Line line = new Line();
    StringBuilder builder = new StringBuilder("d");
    handler.matchToEnd(line, builder);
  }

  @Test
  public void matchWithEndOfLine() {
    SingleLineCommentHandler handler = new SingleLineCommentHandler("//");
    Line line = new Line();
    StringBuilder builder = new StringBuilder("import java.util.*;  //");
    assertTrue(handler.matchToBegin(line, builder));
    builder.append('N');
    assertFalse(handler.matchToEnd(line, builder));
    builder.append('O');
    assertFalse(handler.matchToEnd(line, builder));
    assertTrue(handler.matchWithEndOfLine(line, builder));
    assertEquals("//NO", line.getComment());
  }

  @Test
  public void matchToBegin() {
    SingleLineCommentHandler handler = new SingleLineCommentHandler("//", "*//");
    assertFalse(handler.matchToBegin(new Line(), new StringBuilder("import java.util.*;")));
    assertFalse(handler.matchToBegin(new Line(), new StringBuilder("")));
    assertTrue(handler.matchToBegin(new Line(), new StringBuilder("import java.util.*;  //")));
    assertFalse(handler.matchToBegin(new Line(), new StringBuilder("/*import java.util.*;  *//")));
  }

  @Test
  public void matchToBeginWithDoubleDash() {
    SingleLineCommentHandler handler = new SingleLineCommentHandler("--");
    assertFalse(handler.matchToBegin(new Line(), new StringBuilder("//")));
    assertTrue(handler.matchToBegin(new Line(), new StringBuilder("--")));
  }
}
