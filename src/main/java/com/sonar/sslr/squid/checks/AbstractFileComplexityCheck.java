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
package com.sonar.sslr.squid.checks;

import org.sonar.api.utils.SonarException;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

public abstract class AbstractFileComplexityCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> {

  // See SONAR-3164
  public abstract int getMaximumFileComplexity();

  public abstract MetricDef getComplexityMetric();

  @Override
  public void init() {
    if (getMaximumFileComplexity() <= 0) {
      throw new SonarException("[AbstractFileComplexityCheck] The complexity threshold must be set to a value greater than 0 ("
          + getMaximumFileComplexity()
          + " given).");
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    SourceFile sourceFile = (SourceFile) getContext().peekSourceCode();
    int fileComplexity = ChecksHelper.getRecursiveMeasureInt(sourceFile, getComplexityMetric());

    if (fileComplexity > getMaximumFileComplexity()) {
      getContext().createFileViolation(this, "The file is too complex ({0} while maximum allowed is set to {1}).", fileComplexity,
          getMaximumFileComplexity());
    }
  }

}
