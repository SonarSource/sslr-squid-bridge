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

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.squid.measures.MetricDef;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractFileComplexityCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractFileComplexityCheck<Grammar> {

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
