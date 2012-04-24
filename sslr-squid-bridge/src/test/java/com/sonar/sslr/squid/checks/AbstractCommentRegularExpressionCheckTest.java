/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.test.miniC.MiniCGrammar;
import org.junit.Test;

import static com.sonar.sslr.squid.metrics.ResourceParser.*;
import static com.sonar.sslr.test.squid.CheckMatchers.*;

public class AbstractCommentRegularExpressionCheckTest {

  private static class EmptyCommentRegularExpressionCheck extends AbstractCommentRegularExpressionCheck<MiniCGrammar> {

    @Override
    public String getRegularExpression() {
      return "";
    }

    @Override
    public String getMessage() {
      return "Empty regular expression.";
    }

  }

  @Test
  public void emptyCommentRegularExpresssionCheck() {
    setCurrentSourceFile(scanFile("/checks/commentRegularExpression.mc", new EmptyCommentRegularExpressionCheck()));

    assertNoViolation();
  }

  private static class CaseInsensitiveCommentRegularExpressionWithResultsCheck extends AbstractCommentRegularExpressionCheck<MiniCGrammar> {

    @Override
    public String getRegularExpression() {
      return "(?i).*TODO.*";
    }

    @Override
    public String getMessage() {
      return "Avoid TODO.";
    }

  }

  @Test
  public void caseInsensitiveCommentRegularExpressionWithResultsCheck() {
    setCurrentSourceFile(scanFile("/checks/commentRegularExpression.mc", new CaseInsensitiveCommentRegularExpressionWithResultsCheck()));

    assertNumberOfViolations(3);

    assertViolation().atLine(3).withMessage("Avoid TODO.");
    assertViolation().atLine(5);
    assertViolation().atLine(7);
  }

  private static class CaseSensitiveCommentRegularExpressionWithResultsCheck extends AbstractCommentRegularExpressionCheck<MiniCGrammar> {

    @Override
    public String getRegularExpression() {
      return ".*TODO.*";
    }

    @Override
    public String getMessage() {
      return "Avoid TODO.";
    }

  }

  @Test
  public void caseSensitiveCommentRegularExpressionWithResultsCheck() {
    setCurrentSourceFile(scanFile("/checks/commentRegularExpression.mc", new CaseSensitiveCommentRegularExpressionWithResultsCheck()));

    assertNumberOfViolations(1);

    assertViolation().atLine(3).withMessage("Avoid TODO.");
  }

}
