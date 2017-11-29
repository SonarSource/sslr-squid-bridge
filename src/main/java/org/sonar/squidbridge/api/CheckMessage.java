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

import java.text.MessageFormat;
import java.util.Locale;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CheckMessage {

  private Integer line;
  private Double cost;
  private SourceCode sourceCode;
  private final Object check;
  private final String defaultMessage;
  private final Object[] messageArguments;
  private Boolean bypassExclusion;

  public CheckMessage(Object check, String message, Object... messageArguments) {
    this.check = check;
    this.defaultMessage = message;
    this.messageArguments = messageArguments;
  }

  public void setSourceCode(SourceCode sourceCode) {
    this.sourceCode = sourceCode;
  }

  public SourceCode getSourceCode() {
    return sourceCode;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public Integer getLine() {
    return line;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public Double getCost() {
    return cost;
  }

  public void setBypassExclusion(boolean bypassExclusion) {
    this.bypassExclusion = bypassExclusion;
  }

  public boolean isBypassExclusion() {
    return bypassExclusion == null ? false : bypassExclusion;
  }

  public Object getCheck() {
    return check;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }

  public Object[] getMessageArguments() {
    return messageArguments;
  }

  public String getText(Locale locale) {
    return formatDefaultMessage();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("source", sourceCode).append("check", check).append("msg", defaultMessage)
      .append("line", line).toString();
  }

  public String formatDefaultMessage() {
    if (messageArguments.length == 0) {
      return defaultMessage;
    } else {
      return MessageFormat.format(defaultMessage, messageArguments);
    }
  }

}
