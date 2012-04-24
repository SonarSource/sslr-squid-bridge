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

public class AbstractParseErrorCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractParseErrorCheck<MiniCGrammar> {
  }

  @Test
  public void parseError() {
    checkMessagesVerifier.verify(scanFile("/checks/parse_error.mc", new Check()).getCheckMessages())
        .next().atLine(3);
  }
}
