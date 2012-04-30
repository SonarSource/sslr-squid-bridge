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
package com.sonar.sslr.squid.checks;

import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.test.miniC.MiniCGrammar;
import com.sonar.sslr.test.miniC.MiniCLexer;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;

public class AbstractMagicCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  private static class Check extends AbstractMagicCheck<MiniCGrammar> {

    @Override
    public Set<AstNodeType> getPatterns() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) MiniCLexer.Literals.INTEGER));
    }

    @Override
    public Set<AstNodeType> getInclusions() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) getContext().getGrammar().whileStatement));
    }

    @Override
    public Set<AstNodeType> getExclusions() {
      return Collections.unmodifiableSet(Sets.newHashSet((AstNodeType) getContext().getGrammar().variableInitializer));
    }

    @Override
    public boolean isExcepted(AstNode candidate) {
      return "1337".equals(candidate.getTokenOriginalValue());
    }

    @Override
    public String getMessage() {
      return "Avoid magic stuff.";
    }

  }

  @Test
  public void detected() {
    checkMessagesVerifier.verify(scanFile("/checks/magic.mc", new Check()).getCheckMessages())
        .next().atLine(5).withMessage("Avoid magic stuff.")
        .next().atLine(9);
  }

}
