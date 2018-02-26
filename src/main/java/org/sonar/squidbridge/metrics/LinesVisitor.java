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
package org.sonar.squidbridge.metrics;

import org.sonar.squidbridge.measures.MetricDef;

import org.sonar.squidbridge.SquidAstVisitor;
import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import static com.sonar.sslr.api.GenericTokenType.EOF;

/**
 * Visitor that computes the number of lines of a file.
 */
public class LinesVisitor<G extends Grammar> extends SquidAstVisitor<G> implements AstAndTokenVisitor {

  private final MetricDef metric;

  public LinesVisitor(MetricDef metric) {
    this.metric = metric;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitToken(Token token) {
    if (EOF.equals(token.getType())) {
      getContext().peekSourceCode().setMeasure(metric, token.getLine());
    }
  }

}
