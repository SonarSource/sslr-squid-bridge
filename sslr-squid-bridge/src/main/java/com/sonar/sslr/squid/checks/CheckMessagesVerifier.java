/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.google.common.collect.Ordering;
import org.hamcrest.Matcher;
import org.sonar.squid.api.CheckMessage;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import static org.junit.Assert.assertThat;

/**
 * Helper class for testing checks without having to deploy them on a Sonar instance.
 * It can be used as following:
 * <pre>{@code
 * CheckMessagesVerifier.verify(messages)
 *   .hasNext().atLine(1).withMessage("foo")
 *   .hasNext().atLine(2).withMessage("bar")
 *   .noMore();
 * }</pre>
 * Strictly speaking this is just a wrapper over collection of {@link CheckMessage},
 * which guarantees order of traversal.
 */
public final class CheckMessagesVerifier {

  public static CheckMessagesVerifier verify(Collection<CheckMessage> messages) {
    return new CheckMessagesVerifier(messages);
  }

  private final Iterator<CheckMessage> iterator;
  private CheckMessage current;

  private static final Ordering<CheckMessage> ORDERING = new Ordering<CheckMessage>() {
    public int compare(CheckMessage o1, CheckMessage o2) {
      return o1.getLine() - o2.getLine();
    }
  };

  private static final Ordering<CheckMessage> ORDERING2 = new Ordering<CheckMessage>() {
    public int compare(CheckMessage o1, CheckMessage o2) {
      return o1.getDefaultMessage().compareTo(o2.getDefaultMessage());
    }
  };

  private CheckMessagesVerifier(Collection<CheckMessage> messages) {
    iterator = ORDERING.compound(ORDERING2).sortedCopy(messages).iterator();
  }

  public CheckMessagesVerifier hasNext() {
    if (!iterator.hasNext()) {
      throw new AssertionError("\nExpected violation");
    }
    current = iterator.next();
    return this;
  }

  public void noMore() {
    if (iterator.hasNext()) {
      throw new AssertionError("\nNo more violations expected\ngot:" + iterator.next());
    }
  }

  private void checkStateOfCurrent() {
    if (current == null) {
      throw new IllegalStateException("Prior to this method you should call hasNext()");
    }
  }

  public CheckMessagesVerifier atLine(int expectedLine) {
    checkStateOfCurrent();
    if (expectedLine != current.getLine()) {
      throw new AssertionError("\nExpected: " + expectedLine + "\ngot: " + current.getLine());
    }
    return this;
  }

  public CheckMessagesVerifier withMessage(String expectedMessage) {
    checkStateOfCurrent();
    String actual = current.getText(Locale.ENGLISH);
    if (!actual.equals(expectedMessage)) {
      throw new AssertionError("\nExpected: \"" + expectedMessage + "\"\ngot: \"" + actual + "\"");
    }
    return this;
  }

  /**
   * Note that this method requires JUnit and Hamcrest.
   */
  public CheckMessagesVerifier withMessage(Matcher<String> matcher) {
    checkStateOfCurrent();
    String actual = current.getText(Locale.ENGLISH);
    assertThat(actual, matcher);
    return this;
  }

}
