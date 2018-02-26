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

import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;

import org.sonar.squidbridge.api.CheckMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public class CheckMessagesVerifierRuleTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldNotFailIfNothingToVerify() {
    CheckMessagesVerifierRule rule = new CheckMessagesVerifierRule();
    rule.verify();
  }

  @Test
  public void shouldNotFailIfVerificationsWereSuccessful() {
    CheckMessagesVerifierRule rule = new CheckMessagesVerifierRule();
    rule.verify(Collections.EMPTY_LIST);
    rule.verify(Collections.EMPTY_LIST);
    rule.verify();
  }

  @Test
  public void shouldFailIfFirstVerificationFailed() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nNo more violations expected\ngot:");

    Collection<CheckMessage> messages = Arrays.asList(mock(CheckMessage.class));
    CheckMessagesVerifierRule rule = new CheckMessagesVerifierRule();
    rule.verify(messages);
    rule.verify(Collections.EMPTY_LIST);
    rule.verify();
  }

  @Test
  public void shouldFailIfSecondVerificationFailed() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nNo more violations expected\ngot:");

    Collection<CheckMessage> messages = Arrays.asList(mock(CheckMessage.class));
    CheckMessagesVerifierRule rule = new CheckMessagesVerifierRule();
    rule.verify(Collections.EMPTY_LIST);
    rule.verify(messages);
    rule.verify();
  }

}
