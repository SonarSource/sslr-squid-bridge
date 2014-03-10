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
package org.sonar.squidbridge.test.miniC.fakeChecks;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import org.sonar.squidbridge.checks.SquidCheck;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.junit.Rule;
import org.junit.Test;

public class FakeCommentCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private class FakeCommentCheck extends SquidCheck<Grammar> implements AstAndTokenVisitor {
    public void visitToken(Token token) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isComment() && trivia.getToken().getValue().contains("stupid")) {
          getContext().createLineViolation(this, "Be gentle in your comments.", trivia.getToken().getLine());
        }
      }
    }
  }

  @Test
  public void testFakeCommentCheck() {
    checkMessagesVerifier.verify(scanFile("/fakeChecks/fakeComment.mc", new FakeCommentCheck()).getCheckMessages())
      .next().atLine(6);
  }

}
