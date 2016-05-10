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

import org.sonar.squidbridge.AstScannerExceptionHandler;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.RecognitionException;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractParseErrorCheck<G extends Grammar> extends SquidCheck<G> implements AstScannerExceptionHandler {

  @Override
  public void processRecognitionException(RecognitionException e) {
    getContext().createLineViolation(this, e.getMessage(), e.getLine());
  }

  @Override
  public void processException(Exception e) {
    StringWriter exception = new StringWriter();
    e.printStackTrace(new PrintWriter(exception));
    getContext().createLineViolation(this, exception.toString(), 1);
  }

}
