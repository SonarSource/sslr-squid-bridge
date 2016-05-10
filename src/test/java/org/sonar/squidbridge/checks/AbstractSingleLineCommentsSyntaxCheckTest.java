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

import org.sonar.squidbridge.checks.AbstractSingleLineCommentsSyntaxCheck;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;

public class AbstractSingleLineCommentsSyntaxCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractSingleLineCommentsSyntaxCheck<Grammar> {

    @Override
    public String getSingleLineCommentSyntaxPrefix() {
      return "//";
    }

  }

  @Test
  public void singleLineCommentsSyntax() {
    checkMessagesVerifier.verify(scanFile("/checks/single_line_comments_syntax.mc", new Check()).getCheckMessages())
      .next().atLine(1).withMessage("This single line comment should use the single line comment syntax \"//\"")
      .next().atLine(15);
  }

}

