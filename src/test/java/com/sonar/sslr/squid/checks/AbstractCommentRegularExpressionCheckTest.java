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

public class AbstractCommentRegularExpressionCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

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
    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", new EmptyCommentRegularExpressionCheck()).getCheckMessages());
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
    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", new CaseInsensitiveCommentRegularExpressionWithResultsCheck()).getCheckMessages())
        .next().atLine(3).withMessage("Avoid TODO.")
        .next().atLine(5)
        .next().atLine(7);
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
    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", new CaseSensitiveCommentRegularExpressionWithResultsCheck()).getCheckMessages())
        .next().atLine(3).withMessage("Avoid TODO.");
  }

}
