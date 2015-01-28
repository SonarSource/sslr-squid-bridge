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
package org.sonar.squidbridge.rules;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;

import java.io.IOException;
import java.net.URL;

@Beta
public class ExternalDescriptionLoader {

  private final String resourceBasePath;

  public ExternalDescriptionLoader(NewRepository repository, String resourceBasePath) {
    this.resourceBasePath = resourceBasePath;
  }

  public static void loadHtmlDescriptions(NewRepository repository, String languageKey) {
    ExternalDescriptionLoader loader = new ExternalDescriptionLoader(repository, languageKey);
    for (NewRule newRule : repository.rules()) {
      loader.addHtmlDescription(newRule);
    }
  }

  public void addHtmlDescription(NewRule rule) {
    URL resource = ExternalDescriptionLoader.class.getResource(resourceBasePath + "/" + rule.key() + ".html");
    if (resource != null) {
      addHtmlDescription(rule, resource);
    }
  }

  @VisibleForTesting
  void addHtmlDescription(NewRule rule, URL resource) {
    try {
      rule.setHtmlDescription(Resources.toString(resource, Charsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read: " + resource, e);
    }
  }

}
