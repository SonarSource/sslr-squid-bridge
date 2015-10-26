/*
 * SSLR Squid Bridge
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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

import org.sonar.squidbridge.checks.AbstractNestedCommentsCheck;
import org.sonar.squidbridge.checks.CheckMessagesVerifierRule;
import com.google.common.collect.Sets;
import com.sonar.sslr.api.Grammar;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

public class AbstractNestedCommentsCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractNestedCommentsCheck<Grammar> {

    private static final Set<String> COMMENT_START_TAGS = Collections.unmodifiableSet(Sets.newHashSet("/*", "//"));

    @Override
    public Set<String> getCommentStartTags() {
      return COMMENT_START_TAGS;
    }

  }

  @Test
  public void singleLineCommentsSyntax() {
    checkMessagesVerifier.verify(scanFile("/checks/nested_comments.mc", new Check()).getCheckMessages())
      .next().atLine(1).withMessage("This comments contains the nested comment start tag \"/*\"")
      .next().atLine(2).withMessage("This comments contains the nested comment start tag \"//\"");
  }

}
