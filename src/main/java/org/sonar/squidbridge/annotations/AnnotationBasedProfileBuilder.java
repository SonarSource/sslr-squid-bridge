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
package org.sonar.squidbridge.annotations;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.ValidationMessages;

import java.util.Collection;

public class AnnotationBasedProfileBuilder {

  private final RuleFinder ruleFinder;

  public AnnotationBasedProfileBuilder(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  public RulesProfile build(String repositoryKey, String profileName, String language, Collection<Class<?>> annotatedClasses, ValidationMessages messages) {
    RulesProfile profile = RulesProfile.create(profileName, language);
    for (Class<?> ruleClass : annotatedClasses) {
      addRule(ruleClass, profile, repositoryKey, messages);
    }
    return profile;
  }

  private void addRule(Class<?> ruleClass, RulesProfile profile, String repositoryKey, ValidationMessages messages) {
    if (AnnotationUtils.getAnnotation(ruleClass, ActivatedByDefault.class) != null) {
      org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getAnnotation(ruleClass, org.sonar.check.Rule.class);
      if (ruleAnnotation == null) {
        messages.addWarningText("Class " + ruleClass + " has no Rule annotation");
        return;
      }
      String ruleKey = ruleAnnotation.key();
      Rule rule = ruleFinder.findByKey(repositoryKey, ruleKey);
      if (rule == null) {
        messages.addWarningText("Rule not found: [repository=" + repositoryKey + ", key=" + ruleKey + "]");
      } else {
        profile.activateRule(rule, null);
      }
    }
  }

}
