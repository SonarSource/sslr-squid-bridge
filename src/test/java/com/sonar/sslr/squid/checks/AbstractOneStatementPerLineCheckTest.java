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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractOneStatementPerLineCheckTest {

  @org.junit.Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractOneStatementPerLineCheck<MiniCGrammar> {

    @Override
    public Rule getStatementRule() {
      return getContext().getGrammar().statement;
    }

    @Override
    public boolean isExcluded(AstNode statementNode) {
      return statementNode.getChild(0).is(getContext().getGrammar().compoundStatement);
    }

  }

  @Test
  public void detected() {
    checkMessagesVerifier.verify(scanFile("/checks/one_statement_per_line.mc", new Check()).getCheckMessages())
        .next().atLine(7).withMessage("At most one statement is allowed per line, but 2 statements were found on this line.");
  }

}
