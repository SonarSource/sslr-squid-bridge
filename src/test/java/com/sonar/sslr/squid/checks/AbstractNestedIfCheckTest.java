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

import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractNestedIfCheckTest {

  @org.junit.Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractNestedIfCheck<Grammar> {

    public int maximumNestingLevel = 3;

    @Override
    public int getMaximumNestingLevel() {
      return maximumNestingLevel;
    }

    @Override
    public AstNodeType getIfRule() {
      return MiniCGrammar.IF_STATEMENT;
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
      .next().atLine(27);
  }

}
