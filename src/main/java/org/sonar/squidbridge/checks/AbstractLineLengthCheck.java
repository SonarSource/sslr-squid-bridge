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
package org.sonar.squidbridge.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import org.sonar.api.utils.SonarException;

public abstract class AbstractLineLengthCheck<G extends Grammar> extends SquidCheck<G> implements AstAndTokenVisitor {

  private int lastIncorrectLine;

  // See SONAR-3164
  public abstract int getMaximumLineLength();

  @Override
  public void init() {
    if (getMaximumLineLength() <= 0) {
      throw new SonarException("The maximal line length must be set to a value greater than 0, but given: " + getMaximumLineLength());
    }
  }

  @Override
  public void visitFile(AstNode astNode) {
    lastIncorrectLine = -1;
  }

  @Override
  public void visitToken(Token token) {
    if (!token.isGeneratedCode() && lastIncorrectLine != token.getLine() && token.getColumn() + token.getValue().length() > getMaximumLineLength()) {
      lastIncorrectLine = token.getLine();
      getContext().createLineViolation(this, "The line length is greater than {0,number,integer} authorized.", token.getLine(), getMaximumLineLength());
    }
  }

}
