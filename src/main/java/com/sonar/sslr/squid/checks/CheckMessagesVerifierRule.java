/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.google.common.collect.Lists;
import org.junit.rules.Verifier;
import org.sonar.squid.api.CheckMessage;

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
  protected void verify() throws Throwable {
    for (CheckMessagesVerifier verifier : verifiers) {
      verifier.noMore();
    }
  }

}
