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
