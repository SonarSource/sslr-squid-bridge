/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package org.sonar.squidbridge.commonrules.api;

import org.sonar.api.ExtensionProvider;
import org.sonar.api.ServerExtension;
import org.sonar.squidbridge.commonrules.internal.DefaultCommonRulesRepository;

import java.util.Arrays;
import java.util.List;

/**
 * This class should be extended by any plugin that wants to use some Common Rules.
 * @deprecated common rules are integrated to SonarQube 5.2. It's transparent for plugins that use this Decorator, so
 * that they can support SQ 4.5 LTS and 5.x at the same time.
 */
@Deprecated
public abstract class CommonRulesEngine extends ExtensionProvider implements ServerExtension {

  private final String language;

  public CommonRulesEngine(String language) {
    this.language = language;
  }

  public String language() {
    return language;
  }

  protected abstract void doEnableRules(CommonRulesRepository repository);

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public List provide() {
    return Arrays.asList(newRepository());
  }

  public CommonRulesRepository newRepository() {
    CommonRulesRepository repository = new DefaultCommonRulesRepository(language);
    doEnableRules(repository);
    return repository;
  }
}
