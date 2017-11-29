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

import com.google.common.base.Strings;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractCommentRegularExpressionCheck<G extends Grammar> extends SquidCheck<G> implements AstAndTokenVisitor {

  private Pattern pattern = null;

  // See SONAR-3164
  public abstract String getRegularExpression();

  // See SONAR-3164
  public abstract String getMessage();

  @Override
  public void init() {
    String regularExpression = getRegularExpression();
    checkNotNull(regularExpression, "getRegularExpression() should not return null");

    if (!Strings.isNullOrEmpty(regularExpression)) {
      try {
        pattern = Pattern.compile(regularExpression, Pattern.DOTALL);
      } catch (RuntimeException e) {
        throw new IllegalStateException("Unable to compile regular expression: " + regularExpression, e);
      }
    }
  }

  @Override
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
