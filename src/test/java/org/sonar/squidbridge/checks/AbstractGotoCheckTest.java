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
package org.sonar.squidbridge.checks;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

import org.sonar.squidbridge.checks.AbstractGotoCheck;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

public class AbstractGotoCheckTest {

  @org.junit.Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractGotoCheck<Grammar> {

    @Override
    public AstNodeType getGotoRule() {
      return MiniCGrammar.BREAK_STATEMENT;
    }

  }

  @Test
  public void detected() {
    checkMessagesVerifier.verify(scanFile("/checks/goto.mc", new Check()).getCheckMessages())
      .next().atLine(9).withMessage("Goto should be avoided.");
  }

}
