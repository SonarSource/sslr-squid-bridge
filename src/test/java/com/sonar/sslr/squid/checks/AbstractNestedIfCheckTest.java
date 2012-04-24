/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.Rule;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractNestedIfCheckTest {

  @org.junit.Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractNestedIfCheck<MiniCGrammar> {

    public int maximumNestingLevel = 3;

    @Override
    public int getMaximumNestingLevel() {
      return maximumNestingLevel;
    }

    @Override
    public Rule getIfRule() {
      return getContext().getGrammar().ifStatement;
    }

  }

  @Test
  public void nestedIfWithDefaultNesting() {
    checkMessagesVerifier.verify(scanFile("/checks/nested_if.mc", new Check()).getCheckMessages())
        .next().atLine(9).withMessage("This if has a nesting level of 4, which is higher than the maximum allowed 3.");
  }

  @Test
  public void nestedIfWithSpecificNesting() {
    Check check = new Check();
    check.maximumNestingLevel = 2;

    checkMessagesVerifier.verify(scanFile("/checks/nested_if.mc", check).getCheckMessages())
        .next().atLine(7)
        .next().atLine(9)
        .next().atLine(27);
  }

}
