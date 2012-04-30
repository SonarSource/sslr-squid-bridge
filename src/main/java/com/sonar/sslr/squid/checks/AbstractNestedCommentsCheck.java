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

import java.util.Set;

public abstract class AbstractNestedCommentsCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  public abstract Set<String> getCommentStartTags();

  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment()) {
        String contents = getContext().getCommentAnalyser().getContents(trivia.getToken().getOriginalValue());

        for (String commentStartTag : getCommentStartTags()) {
          if (contents.contains(commentStartTag)) {
            getContext().createLineViolation(this, "This comments contains the nested comment start tag \"{0}\"", trivia.getToken(), commentStartTag);
            break;
          }
        }
      }
    }
  }

}