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
package org.sonar.squidbridge.checks;

import com.google.common.base.Strings;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.xpath.api.AstNodeXPathQuery;
import org.sonar.api.utils.SonarException;

import java.util.List;

public abstract class AbstractXPathCheck<G extends Grammar> extends SquidCheck<G> {

  private AstNodeXPathQuery<Object> query = null;

  // See SONAR-3164
  public abstract String getXPathQuery();

  // See SONAR-3164
  public abstract String getMessage();

  @Override
  public void init() {
    String xpath = getXPathQuery();
    if (!Strings.isNullOrEmpty(xpath)) {
      try {
        query = AstNodeXPathQuery.create(getXPathQuery());
      } catch (RuntimeException e) {
        throw new SonarException("Unable to initialize the XPath engine, perhaps because of an invalid query: " + xpath, e);
      }
    }
  }

  @Override
  public void visitFile(AstNode fileNode) {
    if (query != null && fileNode != null) {
      List<Object> objects = query.selectNodes(fileNode);

      for (Object object : objects) {
        if (object instanceof AstNode) {
          AstNode astNode = (AstNode) object;
          getContext().createLineViolation(this, getMessage(), astNode.getTokenLine());
        } else if (object instanceof Boolean && (Boolean) object) {
          getContext().createFileViolation(this, getMessage());
        }
      }
    }
  }

}
