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
package org.sonar.squidbridge.indexer;

import org.sonar.squidbridge.api.Query;
import org.sonar.squidbridge.api.SourceCode;
import org.sonar.squidbridge.api.SourceCodeIndexer;
import org.sonar.squidbridge.api.SourceCodeSearchEngine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SquidIndex implements SourceCodeIndexer, SourceCodeSearchEngine {

  private final Map<String, SourceCode> index = new TreeMap<String, SourceCode>();

  public Collection<SourceCode> search(Query... query) {
    Set<SourceCode> result = new HashSet<SourceCode>();
    for (SourceCode unit : index.values()) {
      if (isSquidUnitMatchQueries(unit, query)) {
        result.add(unit);
      }
    }
    return result;
  }

  private boolean isSquidUnitMatchQueries(SourceCode unit, Query... queries) {
    boolean match;
    for (Query query : queries) {
      match = query.match(unit);
      if (!match) {
        return false;
      }
    }
    return true;
  }

  @Override
  public SourceCode search(String key) {
    return index.get(key);
  }

  public void index(SourceCode sourceCode) {
    sourceCode.setSourceCodeIndexer(this);
    index.put(sourceCode.getKey(), sourceCode);
  }
}
