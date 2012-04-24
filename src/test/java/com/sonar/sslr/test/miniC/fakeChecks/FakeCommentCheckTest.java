/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.test.miniC.fakeChecks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.squid.checks.CheckMessagesVerifier;
import com.sonar.sslr.squid.checks.SquidCheck;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class FakeCommentCheckTest {

  private class FakeCommentCheck extends SquidCheck<MiniCGrammar> implements AstAndTokenVisitor {

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
    CheckMessagesVerifier.verify(scanFile("/fakeChecks/fakeComment.mc", new FakeCommentCheck()).getCheckMessages())
        .next().atLine(6)
        .noMore();
  }

}
