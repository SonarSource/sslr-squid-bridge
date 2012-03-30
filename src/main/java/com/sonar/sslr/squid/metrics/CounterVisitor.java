/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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

public final class CounterVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {

  private final MetricDef metric;
  private final Set<AstNodeType> astNodeTypes;

  public static <GRAMMAR extends Grammar> Builder<GRAMMAR> builder() {
    return new Builder<GRAMMAR>();
  }

  public static final class Builder<GRAMMAR extends Grammar> {

    private MetricDef metric;
    private Set<AstNodeType> astNodeTypes = Sets.newHashSet();

    private Builder() {
    }

    public Builder<GRAMMAR> setMetricDef(MetricDef metric) {
      this.metric = metric;
      return this;
    }

    public Builder<GRAMMAR> subscribeTo(AstNodeType... astNodeTypes) {
      for (AstNodeType astNodeType : astNodeTypes) {
        this.astNodeTypes.add(astNodeType);
      }

      return this;
    }

    public Builder<GRAMMAR> subscribeTo(Collection<AstNodeType> astNodeTypes) {
      this.astNodeTypes = ImmutableSet.of(astNodeTypes.toArray(new AstNodeType[astNodeTypes.size()]));
      return this;
    }

    public CounterVisitor<GRAMMAR> build() {
      return new CounterVisitor<GRAMMAR>(this);
    }

  }

  private CounterVisitor(Builder<GRAMMAR> builder) {
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
