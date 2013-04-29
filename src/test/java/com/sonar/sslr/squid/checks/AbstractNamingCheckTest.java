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
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractNamingCheckTest {

  @org.junit.Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractNamingCheck<MiniCGrammar> {

    @Override
    public AstNodeType[] getRules() {
      return new AstNodeType[] {getContext().getGrammar().binFunctionDefinition, getContext().getGrammar().binVariableDefinition};
    }

    @Override
    public String getName(AstNode astNode) {
      return astNode.getTokenValue();
    }

    @Override
    public String getRegexp() {
      return "[a-z]+";
    }

    @Override
    public String getMessage(String name) {
      return "\"" + name + "\" is a bad name.";
    }

    @Override
    public boolean isExcluded(AstNode astNode) {
      return "LINE".equals(astNode.getTokenValue());
    }

  }

  @Test
  public void detected() {
    checkMessagesVerifier.verify(scanFile("/checks/naming.mc", new Check()).getCheckMessages())
        .next().atLine(5).withMessage("\"BAD\" is a bad name.")
        .next().atLine(12).withMessage("\"myFunction\" is a bad name.");
  }

}
