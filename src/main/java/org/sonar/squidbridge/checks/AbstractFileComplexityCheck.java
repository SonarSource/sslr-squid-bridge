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
package org.sonar.squidbridge.checks;

import org.sonar.squidbridge.api.SourceFile;

import org.sonar.squidbridge.measures.MetricDef;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.api.utils.SonarException;

public abstract class AbstractFileComplexityCheck<G extends Grammar> extends SquidCheck<G> {

  // See SONAR-3164
  public abstract int getMaximumFileComplexity();

  public abstract MetricDef getComplexityMetric();

  @Override
  public void init() {
    if (getMaximumFileComplexity() <= 0) {
      throw new SonarException("The complexity threshold must be set to a value greater than 0, but given: " + getMaximumFileComplexity());
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    SourceFile sourceFile = (SourceFile) getContext().peekSourceCode();
    int fileComplexity = ChecksHelper.getRecursiveMeasureInt(sourceFile, getComplexityMetric());
    if (fileComplexity > getMaximumFileComplexity()) {
      getContext().createFileViolation(this, "The file is too complex ({0} while maximum allowed is set to {1}).", fileComplexity, getMaximumFileComplexity());
    }
  }

}
