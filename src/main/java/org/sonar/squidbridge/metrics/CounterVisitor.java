/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.measures.MetricDef;

import java.util.Collection;
import java.util.Set;

public final class CounterVisitor<G extends Grammar> extends SquidAstVisitor<G> {

  private final MetricDef metric;
  private final Set<AstNodeType> astNodeTypes;

  public static final class Builder<G extends Grammar> {

    private MetricDef metric;
    private Set<AstNodeType> astNodeTypes = Sets.newHashSet();

    private Builder() {
    }

    public Builder<G> setMetricDef(MetricDef metric) {
      this.metric = metric;
      return this;
    }

    public Builder<G> subscribeTo(AstNodeType... astNodeTypes) {
      for (AstNodeType astNodeType : astNodeTypes) {
        this.astNodeTypes.add(astNodeType);
      }

      return this;
    }

    public Builder<G> subscribeTo(Collection<AstNodeType> astNodeTypes) {
      this.astNodeTypes = Sets.newHashSet(astNodeTypes);
      return this;
    }

    public CounterVisitor<G> build() {
      return new CounterVisitor<G>(this);
    }

  }

  private CounterVisitor(Builder<G> builder) {
    this.metric = builder.metric;
    this.astNodeTypes = ImmutableSet.copyOf(builder.astNodeTypes);
  }

  public static <G extends Grammar> Builder<G> builder() {
    return new Builder<G>();
  }

  @Override
  public void init() {
    for (AstNodeType astNodeType : astNodeTypes) {
      subscribeTo(astNodeType);
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    getContext().peekSourceCode().add(metric, 1);
  }

}
