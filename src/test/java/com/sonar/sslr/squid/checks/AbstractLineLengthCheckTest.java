/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractLineLengthCheckTest {

  private static class Check extends AbstractLineLengthCheck<MiniCGrammar> {

    public int maximumLineLength = 80;

    @Override
    public int getMaximumLineLength() {
      return maximumLineLength;
    }

  }

  @Test
  public void lineLengthWithDefaultLength() {
    CheckMessagesVerifier.verify(scanFile("/checks/line_length.mc", new Check()).getCheckMessages())
        .next().atLine(3).withMessage("The line length is greater than 80 authorized.")
        .noMore();
  }

  @Test
  public void lineLengthWithSpecificLength() {
    Check check = new Check();
    check.maximumLineLength = 7;

    CheckMessagesVerifier.verify(scanFile("/checks/line_length.mc", check).getCheckMessages())
        .next().atLine(3)
        .next().atLine(4)
        .noMore();
  }

}
