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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.indexer.QueryByType;

import com.sonar.sslr.squid.AstScanner;
import com.sonar.sslr.squid.SquidAstVisitor;
import com.sonar.sslr.test.miniC.MiniCAstScanner;
import com.sonar.sslr.test.miniC.MiniCGrammar;

public class ResourceParser {

  public static SourceFile scanFile(String filePath, SquidAstVisitor<MiniCGrammar>... visitors) {
    AstScanner<MiniCGrammar> scanner = MiniCAstScanner.create(visitors);
    File file = FileUtils.toFile(ResourceParser.class.getResource(filePath));
    if (file == null || !file.exists()) {
      throw new IllegalArgumentException("The file located under \"" + filePath + "\" was not found.");
    }
    scanner.scanFile(file);
    assertThat(scanner.getIndex().search(new QueryByType(SourceFile.class)).size(), is(1));
    return (SourceFile) scanner.getIndex().search(new QueryByType(SourceFile.class)).iterator().next();
  }

}