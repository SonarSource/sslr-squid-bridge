/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.squid.api.CheckMessage;

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
