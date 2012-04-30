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
import org.apache.commons.lang.StringUtils;

public abstract class AbstractSingleLineCommentsSyntaxCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  public abstract String getSingleLineCommentSyntaxPrefix();

  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment() && trivia.getToken().getLine() < token.getLine()) {
        String comment = trivia.getToken().getOriginalValue();

        if (!comment.startsWith(getSingleLineCommentSyntaxPrefix()) && !StringUtils.containsAny(comment, "\r\n")) {
          getContext().createLineViolation(this, "This single line comment should use the single line comment syntax \"{0}\"", trivia.getToken(),
              getSingleLineCommentSyntaxPrefix());
        }
      }
    }
  }

}
