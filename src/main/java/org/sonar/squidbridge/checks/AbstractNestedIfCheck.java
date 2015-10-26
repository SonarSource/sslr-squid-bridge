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
package org.sonar.squidbridge.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.utils.SonarException;

public abstract class AbstractNestedIfCheck<G extends Grammar> extends SquidCheck<G> {

  private int nestingLevel;

  // See SONAR-3164
  public abstract int getMaximumNestingLevel();

  public abstract AstNodeType getIfRule();

  @Override
  public void visitFile(AstNode astNode) {
    nestingLevel = 0;
  }

  @Override
  public void init() {
    if (getMaximumNestingLevel() <= 0) {
      throw new SonarException("The maximal if nesting level must be set to a value greater than 0, but given: " + getMaximumNestingLevel());
    }
    subscribeTo(getIfRule());
  }

  @Override
  public void visitNode(AstNode astNode) {
    nestingLevel++;
    if (nestingLevel == getMaximumNestingLevel() + 1) {
      getContext().createLineViolation(this, "This if has a nesting level of {0}, which is higher than the maximum allowed {1}.", astNode,
        nestingLevel,
        getMaximumNestingLevel());
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    nestingLevel--;
  }

}
