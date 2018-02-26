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
package org.sonar.squidbridge.commonrules.internal.checks;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;

import javax.annotation.Nullable;

public abstract class CommonCheck {

  public abstract void checkResource(Resource resource, DecoratorContext context, RuleKey rule, ResourcePerspectives perspectives);

  protected void createIssue(Resource resource, ResourcePerspectives perspectives, RuleKey ruleKey, @Nullable Double effortToFix, String message) {
    Issuable issuable = perspectives.as(Issuable.class, resource);
    if (issuable != null) {
      issuable.addIssue(issuable.newIssueBuilder()
        .ruleKey(ruleKey)
        .effortToFix(effortToFix)
        .message(message)
        .build());
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
