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
package org.sonar.squidbridge.rules;

import com.google.common.annotations.Beta;
import org.sonar.api.server.rule.RulesDefinition.NewParam;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Beta
public class PropertyFileLoader {

  private PropertyFileLoader() {
    // This class should not be instantiated
  }

  public static void loadNames(NewRepository repository, String resourceAbsolutePath) {
    InputStream stream = PropertyFileLoader.class.getResourceAsStream(resourceAbsolutePath);
    if (stream == null) {
      throw new IllegalArgumentException("Cound not find resource: " + resourceAbsolutePath);
    }
    loadNames(repository, stream);
  }

  public static void loadNames(NewRepository repository, InputStream stream) {
    Properties properties = new Properties();
    try {
      properties.load(stream);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read names from properties", e);
    }
    for (NewRule rule : repository.rules()) {
      String baseKey = "rule." + repository.key() + "." + rule.key();
      String nameKey = baseKey + ".name";
      String ruleName = properties.getProperty(nameKey);
      if (ruleName != null) {
        rule.setName(ruleName);
      }
      for (NewParam param : rule.params()) {
        String paramDescriptionKey = baseKey + ".param." + param.key();
        String paramDescription = properties.getProperty(paramDescriptionKey);
        if (paramDescription != null) {
          param.setDescription(paramDescription);
        }
      }
    }
  }

}
