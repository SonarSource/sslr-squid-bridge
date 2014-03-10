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
package org.sonar.squidbridge.checks;

import org.sonar.squidbridge.api.CheckMessage;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;
import org.hamcrest.Matcher;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import static org.junit.Assert.assertThat;

/**
 * Helper class for testing checks without having to deploy them on a Sonar instance.
 * It can be used as following:
 * <pre>{@code
 * CheckMessagesVerifier.verify(messages)
 *   .next().atLine(1).withMessage("foo")
 *   .next().atLine(2).withMessage("bar")
 *   .noMore();
 * }</pre>
 * Strictly speaking this is just a wrapper over collection of {@link CheckMessage},
 * which guarantees order of traversal.
 *
 * @see CheckMessagesVerifierRule
 * @since sslr-squid-bridge 2.1
 */
public final class CheckMessagesVerifier {

  public static CheckMessagesVerifier verify(Collection<CheckMessage> messages) {
    return new CheckMessagesVerifier(messages);
  }

  private final Iterator<CheckMessage> iterator;
  private CheckMessage current;

  private static final Comparator<CheckMessage> ORDERING = new Comparator<CheckMessage>() {
    @Override
    public int compare(CheckMessage left, CheckMessage right) {
      if (Objects.equal(left.getLine(), right.getLine())) {
        return left.getDefaultMessage().compareTo(right.getDefaultMessage());
      } else if (left.getLine() == null) {
        return -1;
      } else if (right.getLine() == null) {
        return 1;
      } else {
        return left.getLine().compareTo(right.getLine());
      }
    }
  };

  private CheckMessagesVerifier(Collection<CheckMessage> messages) {
    iterator = Ordering.from(ORDERING).sortedCopy(messages).iterator();
  }

  public CheckMessagesVerifier next() {
    if (!iterator.hasNext()) {
      throw new AssertionError("\nExpected violation");
    }
    current = iterator.next();
    return this;
  }

  public void noMore() {
    if (iterator.hasNext()) {
      CheckMessage next = iterator.next();
      throw new AssertionError("\nNo more violations expected\ngot: at line " + next.getLine());
    }
  }

  private void checkStateOfCurrent() {
    if (current == null) {
      throw new IllegalStateException("Prior to this method you should call next()");
    }
  }

  public CheckMessagesVerifier atLine(@Nullable Integer expectedLine) {
    checkStateOfCurrent();
    if (!Objects.equal(expectedLine, current.getLine())) {
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
  public CheckMessagesVerifier withMessageThat(Matcher<String> matcher) {
    checkStateOfCurrent();
    String actual = current.getText(Locale.ENGLISH);
    assertThat(actual, matcher);
    return this;
  }


  /**
   * @since sslr-squid-bridge 2.3
   */
  public CheckMessagesVerifier withCost(Double expectedCost) {
    checkStateOfCurrent();
    if (!Objects.equal(expectedCost, current.getCost())) {
      throw new AssertionError("\nExpected: " + expectedCost + "\ngot: " + current.getCost());
    }
    return this;
  }

}
