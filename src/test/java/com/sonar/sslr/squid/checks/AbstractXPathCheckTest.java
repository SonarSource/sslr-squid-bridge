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

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Rule;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractXPathCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class EmptyXPathCheck extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "";
    }

    @Override
    public String getMessage() {
      return "Empty XPath check.";
    }

  }

  @Test
  public void emptyXPathCheck() {
    checkMessagesVerifier.verify(scanFile("/checks/xpath.mc", new EmptyXPathCheck()).getCheckMessages());
  }

  private static class BooleanXPathCheckWithResults extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "count(//variableDefinition) > 0";
    }

    @Override
    public String getMessage() {
      return "Boolean XPath rule with results.";
    }

  }

  @Test
  public void booleanXPathCheckWithResults() {
    checkMessagesVerifier.verify(scanFile("/checks/xpath.mc", new BooleanXPathCheckWithResults()).getCheckMessages())
        .next().withMessage("Boolean XPath rule with results.");
  }

  private static class BooleanXPathCheckWithoutResults extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "count(//variableDefinition) > 2";
    }

    @Override
    public String getMessage() {
      return "Boolean XPath rule without results.";
    }

  }

  @Test
  public void booleanXPathCheckWithoutResults() {
    checkMessagesVerifier.verify(scanFile("/checks/xpath.mc", new BooleanXPathCheckWithoutResults()).getCheckMessages());
  }

  private static class AstNodesXpathCheck extends AbstractXPathCheck<MiniCGrammar> {

    @Override
    public String getXPathQuery() {
      return "//variableDefinition";
    }

    @Override
    public String getMessage() {
      return "No variable definitions allowed!";
    }

  }

  @Test
  public void astNodesXpathCheck() {
    checkMessagesVerifier.verify(scanFile("/checks/xpath.mc", new AstNodesXpathCheck()).getCheckMessages())
        .next().atLine(1).withMessage("No variable definitions allowed!")
        .next().atLine(5);
  }

}
