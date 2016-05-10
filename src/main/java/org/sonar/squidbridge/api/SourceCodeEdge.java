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
package org.sonar.squidbridge.api;

import org.sonar.graph.Edge;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SourceCodeEdge implements Edge<SourceCode> {

  private final SourceCode from;
  private final SourceCode to;
  private final SourceCodeEdgeUsage usage;
  private Set<SourceCodeEdge> rootEdges;
  private Set<SourceCode> rootFromNodes;
  private Set<SourceCode> rootToNodes;
  private final int hashcode;
  private SourceCodeEdge parent;

  public SourceCodeEdge(SourceCode from, SourceCode to, SourceCodeEdgeUsage link) {
    this(from, to, link, null);
  }

  public SourceCodeEdge(SourceCode from, SourceCode to, SourceCodeEdgeUsage usage, @Nullable SourceCodeEdge rootEdge) {
    this.hashcode = from.hashCode() * 31 + to.hashCode() + usage.hashCode(); // NOSONAR even if this basic algorithm could be improved
    this.from = from;
    this.to = to;
    this.usage = usage;
    addRootEdge(rootEdge);
  }

  @Override
  public SourceCode getFrom() {
    return from;
  }

  @Override
  public SourceCode getTo() {
    return to;
  }

  public SourceCodeEdgeUsage getUsage() {
    return usage;
  }

  private boolean noRoots() {
    return rootEdges == null;
  }

  public boolean hasAnEdgeFromRootNode(SourceCode rootFromNode) {
    if (noRoots()) {
      return false;
    }
    return rootFromNodes.contains(rootFromNode);
  }

  public boolean hasAnEdgeToRootNode(SourceCode rootToNode) {
    if (noRoots()) {
      return false;
    }
    return rootToNodes.contains(rootToNode);
  }

  public Set<SourceCodeEdge> getRootEdges() {
    return rootEdges;
  }

  public int getNumberOfRootFromNodes() {
    if (noRoots()) {
      return 0;
    }
    return rootFromNodes.size();
  }

  public final void addRootEdge(@Nullable SourceCodeEdge rootRelationShip) {
    if (noRoots()) {
      rootEdges = new HashSet<SourceCodeEdge>();
      rootFromNodes = new HashSet<SourceCode>();
      rootToNodes = new HashSet<SourceCode>();
    }
    if (rootRelationShip != null) {
      rootEdges.add(rootRelationShip);
      rootFromNodes.add(rootRelationShip.getFrom());
      rootToNodes.add(rootRelationShip.getTo());
      rootRelationShip.setParent(this);
    }
  }

  @Override
  public int getWeight() {
    if (noRoots()) {
      return 0;
    }
    return rootEdges.size();
  }

  public SourceCodeEdge getParent() {
    return parent;
  }

  public SourceCodeEdge setParent(SourceCodeEdge parent) {
    this.parent = parent;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SourceCodeEdge) || this.hashCode() != obj.hashCode()) {
      return false;
    }
    SourceCodeEdge edge = (SourceCodeEdge) obj;
    return from.equals(edge.from) && to.equals(edge.to);
  }

  @Override
  public int hashCode() {
    return hashcode;
  }

  @Override
  public String toString() {
    return "from : " + from + ", to : " + to;
  }
}
