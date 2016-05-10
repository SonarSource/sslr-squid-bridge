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
package org.sonar.squidbridge.metrics;

import org.sonar.squidbridge.test.miniC.MiniCAstScanner;

import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.indexer.QueryByType;
import org.sonar.squidbridge.AstScanner;
import org.sonar.squidbridge.SquidAstVisitor;
import com.sonar.sslr.api.Grammar;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class ResourceParser {

  public static SourceFile scanFile(String filePath, SquidAstVisitor<Grammar>... visitors) {
    return scanFile(filePath, false, visitors);
  }

  public static SourceFile scanFileIgnoreHeaderComments(String filePath, SquidAstVisitor<Grammar>... visitors) {
    return scanFile(filePath, true, visitors);
  }

  private static SourceFile scanFile(String filePath, boolean ignoreHeaderComments, SquidAstVisitor<Grammar>... visitors) {
    AstScanner<Grammar> scanner = ignoreHeaderComments ? MiniCAstScanner.createIgnoreHeaderComments(visitors) : MiniCAstScanner
      .create(visitors);
    File file = FileUtils.toFile(ResourceParser.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new IllegalArgumentException("The file located under \"" + filePath + "\" was not found.");
    }
    scanner.scanFile(file);
    assertThat(scanner.getIndex().search(new QueryByType(SourceFile.class)).size()).isEqualTo(1);
    return (SourceFile) scanner.getIndex().search(new QueryByType(SourceFile.class)).iterator().next();
  }

}
