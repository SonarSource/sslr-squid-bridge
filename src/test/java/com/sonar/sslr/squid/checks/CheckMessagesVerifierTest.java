/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.squid.api.CheckMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckMessagesVerifierTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void next() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nExpected violation");

    CheckMessagesVerifier.verify(Collections.EMPTY_LIST)
        .next();
  }

  @Test
  public void noMore() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nNo more violations expected\ngot:");

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .noMore();
  }

  @Test
  public void line() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nExpected: 2\ngot: 1");

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .next().atLine(2);
  }

  @Test
  public void line_withoutHasNext() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Prior to this method you should call hasNext()");

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .atLine(2);
  }

  @Test
  public void withMessage() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(allOf(containsString("Expected: \"bar\""), containsString("got: \"foo\"")));

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .next().atLine(1).withMessage("bar");
  }

  @Test
  public void withMessage_withoutHasNext() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Prior to this method you should call hasNext()");

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .withMessage("foo");
  }

  @Test
  public void messageThat() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage("\nExpected: a string containing \"bar\"\n     got: \"foo\"");

    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"));
    CheckMessagesVerifier.verify(messages)
        .next().atLine(1).messageThat(containsString("bar"));
  }

  @Test
  public void ok() {
    Collection<CheckMessage> messages = Arrays.asList(mockCheckMessage(1, "foo"), mockCheckMessage(1, "bar"));
    CheckMessagesVerifier.verify(messages)
        .next().atLine(1).withMessage("bar")
        .next().atLine(1).messageThat(containsString("foo"))
        .noMore();
  }

  private static final CheckMessage mockCheckMessage(Integer line, String message) {
    CheckMessage checkMessage = mock(CheckMessage.class);
    when(checkMessage.getLine()).thenReturn(line);
    when(checkMessage.getDefaultMessage()).thenReturn(message);
    when(checkMessage.getText(Mockito.any(Locale.class))).thenReturn(message);
    return checkMessage;
  }

}
