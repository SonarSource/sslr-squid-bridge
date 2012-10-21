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
package com.sonar.sslr.squid.metrics;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.SquidAstVisitor;
import org.sonar.squid.measures.MetricDef;

import java.util.Collection;
import java.util.Set;

public final class CounterVisitor<G extends Grammar> extends SquidAstVisitor<G> {

  private final MetricDef metric;
  private final Set<AstNodeType> astNodeTypes;

  public static <G extends Grammar> Builder<G> builder() {
    return new Builder<G>();
  }

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
      this.astNodeTypes = ImmutableSet.of(astNodeTypes.toArray(new AstNodeType[astNodeTypes.size()]));
      return this;
    }

    public CounterVisitor<G> build() {
      return new CounterVisitor<G>(this);
    }

  }

  private CounterVisitor(Builder<G> builder) {
    this.metric = builder.metric;
    this.astNodeTypes = ImmutableSet.of(builder.astNodeTypes.toArray(new AstNodeType[builder.astNodeTypes.size()]));
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
