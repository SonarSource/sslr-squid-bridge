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

import com.google.common.collect.Lists;
import org.junit.rules.Verifier;

import java.util.Collection;
import java.util.List;

/**
 * This JUnit Rule allows to automatically execute {@link CheckMessagesVerifier#noMore()}.
 * <pre>
 * &#064;org.junit.Rule
 * public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();
 *
 * &#064;org.junit.Test
 * public void test() {
 *   checkMessagesVerifier.verify(messages)
 *     .next().atLine(1)
 *     .next().atLine(2);
 * }
 * </pre>
 *
 * @since sslr-squid-bridge 2.1
 */
public class CheckMessagesVerifierRule extends Verifier {

  private final List<CheckMessagesVerifier> verifiers = Lists.newArrayList();

  public CheckMessagesVerifier verify(Collection<CheckMessage> messages) {
    CheckMessagesVerifier verifier = CheckMessagesVerifier.verify(messages);
    verifiers.add(verifier);
    return verifier;
  }

  @Override
  protected void verify() {
    for (CheckMessagesVerifier verifier : verifiers) {
      verifier.noMore();
    }
  }

}
