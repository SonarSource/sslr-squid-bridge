/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.squid.measures.MetricDef;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractFileComplexityCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractFileComplexityCheck<MiniCGrammar> {

    public int maximumFileComplexity = 100;

    @Override
    public int getMaximumFileComplexity() {
      return maximumFileComplexity;
    }

    @Override
    public MetricDef getComplexityMetric() {
      return MiniCMetrics.COMPLEXITY;
    }

  }

  @Test
  public void fileComplexityEqualsMaximum() {
    Check check = new Check();
    check.maximumFileComplexity = 5;

    checkMessagesVerifier.verify(scanFile("/checks/complexity5.mc", check).getCheckMessages());
  }

  @Test
  public void fileComplexityGreaterMaximum() {
    Check check = new Check();
    check.maximumFileComplexity = 4;

    checkMessagesVerifier.verify(scanFile("/checks/complexity5.mc", check).getCheckMessages())
        .next().withMessage("The file is too complex (5 while maximum allowed is set to 4).");
  }

}
