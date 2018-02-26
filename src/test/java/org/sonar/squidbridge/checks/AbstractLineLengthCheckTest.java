/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
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

import org.sonar.squidbridge.checks.AbstractLineLengthCheck;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;

public class AbstractLineLengthCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static class Check extends AbstractLineLengthCheck<Grammar> {

    public int maximumLineLength = 80;

    @Override
    public int getMaximumLineLength() {
      return maximumLineLength;
    }

  }

  private Check check = new Check();

  @Test
  public void lineLengthWithDefaultLength() {
    checkMessagesVerifier.verify(scanFile("/checks/line_length.mc", check).getCheckMessages())
      .next().atLine(3).withMessage("The line length is greater than 80 authorized.");
  }

  @Test
  public void lineLengthWithSpecificLength() {
    check.maximumLineLength = 7;

    checkMessagesVerifier.verify(scanFile("/checks/line_length.mc", check).getCheckMessages())
      .next().atLine(3)
      .next().atLine(4);
  }

  @Test
  public void wrong_parameter() {
    check.maximumLineLength = 0;

    thrown.expect(SonarException.class);
    thrown.expectMessage("The maximal line length must be set to a value greater than 0, but given: 0");
    scanFile("/checks/line_length.mc", check);
  }

}
