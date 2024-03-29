/*
 * SSLR Squid Bridge
 * Copyright (C) 2010-2022 SonarSource SA
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
package org.sonar.squidbridge.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated since 2.7. Will be removed without alternative.
 * Since SQ 5.6 LTS, SQALE is not used in our model.
 * Remediation cost should be registered manually directly when registering a rule, using the following methods from SQ 6.7 LTS API:
 * <ul>
 * <li>org.sonar.api.server.rule.RulesDefinition.NewRule.setDebtRemediationFunction(DebtRemediationFunction)</li>
 * <li>org.sonar.api.server.rule.RulesDefinition.NewRule.setGapDescription(String)</li>
 * </ul>
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqaleLinearRemediation {

  String coeff();

  String effortToFixDescription();

}
