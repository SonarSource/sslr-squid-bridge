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
package org.sonar.squidbridge;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.sonar.graph.DirectedGraph;
import org.sonar.graph.DirectedGraphAccessor;
import org.sonar.squidbridge.api.CodeScanner;
import org.sonar.squidbridge.api.CodeVisitor;
import org.sonar.squidbridge.api.Query;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceCodeEdge;
import org.sonar.squidbridge.api.SourceCodeSearchEngine;
import org.sonar.squidbridge.api.SourceCodeTreeDecorator;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.api.SquidConfiguration;
import org.sonar.squidbridge.indexer.SquidIndex;
import org.sonar.squidbridge.measures.Metric;
import org.sonar.squidbridge.measures.MetricDef;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Squid implements DirectedGraphAccessor<SourceCode, SourceCodeEdge>, SourceCodeSearchEngine {

  private MutablePicoContainer pico;
  private final SourceProject project;
  private final SquidIndex squidIndex;
  private DirectedGraph<SourceCode, SourceCodeEdge> graph = new DirectedGraph<SourceCode, SourceCodeEdge>();
  private final Set<CodeVisitor> externalCodeVisitors = new HashSet<CodeVisitor>();

  public Squid(SquidConfiguration conf) {
    pico = new TransientPicoContainer();
    pico.addComponent(conf);
    project = new SourceProject("Project");
    squidIndex = new SquidIndex();
    squidIndex.index(project);
    pico.addComponent(squidIndex);
    pico.addComponent(project);
    pico.addComponent(graph);
  }

  public Squid() {
    this(new SquidConfiguration());
  }

  public void registerVisitor(CodeVisitor visitor) {
    externalCodeVisitors.add(visitor);
  }

  public void registerVisitor(Class<? extends CodeVisitor> visitor) {
    addToPicocontainer(visitor);
    externalCodeVisitors.add(pico.getComponent(visitor));
  }

  public <S extends CodeScanner> S register(Class<S> scannerClass) {
    if (pico.getComponent(scannerClass) != null) {
      throw new IllegalStateException("The Squid SCANNER '" + scannerClass.getName() + "' can't be registered multiple times.");
    }
    addToPicocontainer(scannerClass);
    S scanner = pico.getComponent(scannerClass);
    for (Object clazz : scanner.getVisitorClasses()) {
      addToPicocontainer((Class) clazz);
      scanner.accept(pico.<CodeVisitor>getComponent((Class) clazz));
    }
    for (CodeVisitor externalVisitor : externalCodeVisitors) {
      scanner.accept(externalVisitor);
    }
    return scanner;
  }

  /**
   * @deprecated use {@link #decorateSourceCodeTreeWith(MetricDef...)} instead
   */
  @Deprecated
  public SourceProject aggregate() {
    return decorateSourceCodeTreeWith(Metric.values());
  }

  public SourceProject decorateSourceCodeTreeWith(MetricDef... metrics) {
    SourceCodeTreeDecorator decorator = new SourceCodeTreeDecorator(project);
    decorator.decorateWith(metrics);
    return project;
  }

  public SourceProject getProject() {
    return project;
  }

  private void addToPicocontainer(Class<?> classToExpose) {
    if (pico.getComponent(classToExpose) == null) {
      pico.addComponent(classToExpose);
    }
  }

  @Override
  public SourceCode search(String key) {
    return squidIndex.search(key);
  }

  @Override
  public Collection<SourceCode> search(Query... query) {
    return squidIndex.search(query);
  }

  @Override
  public SourceCodeEdge getEdge(SourceCode from, SourceCode to) {
    return graph.getEdge(from, to);
  }

  @Override
  public Collection<SourceCodeEdge> getIncomingEdges(SourceCode to) {
    return graph.getIncomingEdges(to);
  }

  @Override
  public Collection<SourceCodeEdge> getOutgoingEdges(SourceCode from) {
    return graph.getOutgoingEdges(from);
  }

  @Override
  public Set<SourceCode> getVertices() {
    return graph.getVertices();
  }

  public List<SourceCodeEdge> getEdges(Collection<SourceCode> vertices) {
    return graph.getEdges(vertices);
  }

  @Override
  public boolean hasEdge(SourceCode from, SourceCode to) {
    return graph.hasEdge(from, to);
  }

  public void flush() {
    graph = null;
    pico = null;
  }

}
