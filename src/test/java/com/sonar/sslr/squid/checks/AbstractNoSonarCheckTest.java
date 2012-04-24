/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Rule;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractNoSonarCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractNoSonarCheck<MiniCGrammar> {
  }

  @Test
  public void singleLineCommentsSyntax() {
    checkMessagesVerifier.verify(scanFile("/checks/no_sonar.mc", new Check()).getCheckMessages())
        .next().atLine(5).withMessage("Is NOSONAR usage acceptable or does it hide a real quality flaw?")
        .next().atLine(6)
        .next().atLine(10);
  }

}
