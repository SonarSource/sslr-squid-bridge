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
package com.sonar.sslr.test.miniC.fakeChecks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.PreprocessingDirective;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.squid.checks.CheckMessagesVerifierRule;
import com.sonar.sslr.squid.checks.SquidCheck;
import com.sonar.sslr.test.miniC.MiniCPreprocessor;
import org.junit.Rule;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class FakePreprocessorCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private class FakePreprocessorCheck extends SquidCheck<Grammar> implements AstAndTokenVisitor {
    public void visitToken(Token token) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isPreprocessor() && trivia.hasPreprocessingDirective()) {
          PreprocessingDirective directive = trivia.getPreprocessingDirective();
          AstNode preprocessorStructure = directive.getAst();
          AstNode definition = preprocessorStructure.findFirstDirectChild(MiniCPreprocessor.MiniCPreprocessorGrammar.BIN_DEFINITION);
          if (definition != null && "WTF".equals(definition.getTokenOriginalValue())) {
            getContext().createLineViolation(this, "Be gentle in your preprocessor definitions.",
              trivia.getPreprocessingDirective().getAst().getTokenLine());
          }
        }
      }
    }
  }

  @Test
  public void testFakeCommentCheck() {
    checkMessagesVerifier.verify(scanFile("/fakeChecks/fakePreprocessor.mc", new FakePreprocessorCheck()).getCheckMessages())
      .next().atLine(2);
  }

}
