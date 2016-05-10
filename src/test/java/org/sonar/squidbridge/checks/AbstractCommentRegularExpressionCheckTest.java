/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.squidbridge.checks;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

import org.sonar.squidbridge.checks.AbstractCommentRegularExpressionCheck;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;

public class AbstractCommentRegularExpressionCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static class Check extends AbstractCommentRegularExpressionCheck<Grammar> {
    private String regularExpression;
    private String message;

    @Override
    public String getRegularExpression() {
      return regularExpression;
    }

    @Override
    public String getMessage() {
      return message;
    }
  }

  private Check check = new Check();

  @Test
  public void empty() {
    check.regularExpression = "";
    check.message = "Empty regular expression.";

    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", check).getCheckMessages());
  }

  @Test
  public void case_insensitive() {
    check.regularExpression = "(?i).*TODO.*";
    check.message = "Avoid TODO.";

    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", check).getCheckMessages())
      .next().atLine(3).withMessage("Avoid TODO.")
      .next().atLine(5)
      .next().atLine(7);
  }

  @Test
  public void case_sensitive() {
    check.regularExpression = ".*TODO.*";
    check.message = "Avoid TODO.";

    checkMessagesVerifier.verify(scanFile("/checks/commentRegularExpression.mc", check).getCheckMessages())
      .next().atLine(3).withMessage("Avoid TODO.");
  }

  @Test
  public void wrong_regular_expression() {
    check.regularExpression = "*";

    thrown.expect(SonarException.class);
    thrown.expectMessage("Unable to compile regular expression: *");
    scanFile("/checks/commentRegularExpression.mc", check);
  }

}
