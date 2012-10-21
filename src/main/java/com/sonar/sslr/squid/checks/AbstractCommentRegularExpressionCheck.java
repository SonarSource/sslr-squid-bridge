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
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.utils.SonarException;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.*;

public abstract class AbstractCommentRegularExpressionCheck<G extends Grammar> extends SquidCheck<G> implements AstAndTokenVisitor {

  // See SONAR-3164
  public abstract String getRegularExpression();

  // See SONAR-3164
  public abstract String getMessage();

  private Pattern pattern = null;

  @Override
  public void init() {
    String regularExpression = getRegularExpression();
    checkNotNull(regularExpression, "getRegularExpression() should not return null");

    if (!"".equals(regularExpression)) {
      try {
        pattern = Pattern.compile(regularExpression, Pattern.DOTALL);
      } catch (RuntimeException e) {
        throw new SonarException("[AbstractCommentRegularExpressionCheck] Unable to compile the regular expression ("
          + regularExpression
          + " given).", e);
      }
    }
  }

  public void visitToken(Token token) {
    if (pattern != null) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isComment() && pattern.matcher(trivia.getToken().getOriginalValue()).matches()) {
          getContext().createLineViolation(this, getMessage(), trivia.getToken());
        }
      }
    }
  }

}
