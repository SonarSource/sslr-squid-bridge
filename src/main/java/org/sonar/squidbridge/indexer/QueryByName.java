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
package org.sonar.squidbridge.indexer;

import org.sonar.squidbridge.api.Query;
import org.sonar.squidbridge.api.SourceCode;

public class QueryByName implements Query {

  private final String resourceName;

  public QueryByName(String resourceName) {
    if (resourceName == null) {
      throw new IllegalStateException("The name can't be null !");
    }
    this.resourceName = resourceName;
  }

  @Override
  public boolean match(SourceCode unit) {
    if (unit.getName() != null) {
      return unit.getName().equals(resourceName);
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QueryByName)) {
      return false;
    }

    QueryByName that = (QueryByName) o;

    if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return resourceName.hashCode();
  }
}
