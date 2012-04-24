/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractSingleLineCommentsSyntaxCheckTest {

  private static class Check extends AbstractSingleLineCommentsSyntaxCheck<MiniCGrammar> {

    @Override
    public String getSingleLineCommentSyntaxPrefix() {
      return "//";
    }

  }

  @Test
  public void singleLineCommentsSyntax() {
    CheckMessagesVerifier.verify(scanFile("/checks/single_line_comments_syntax.mc", new Check()).getCheckMessages())
        .next().atLine(1).withMessage("This single line comment should use the single line comment syntax \"//\"")
        .next().atLine(15)
        .noMore();
  }

}
