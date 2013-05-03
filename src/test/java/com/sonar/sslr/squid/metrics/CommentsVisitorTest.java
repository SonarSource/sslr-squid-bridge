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
package com.sonar.sslr.squid.metrics;

import com.sonar.sslr.test.miniC.MiniCAstScanner.MiniCMetrics;
import org.junit.Test;
import org.sonar.squid.api.SourceFile;

import static com.sonar.sslr.squid.metrics.ResourceParser.scanFile;
import static com.sonar.sslr.squid.metrics.ResourceParser.scanFileIgnoreHeaderComments;
import static org.fest.assertions.Assertions.assertThat;

public class CommentsVisitorTest {

  @Test
  public void empty() {
    SourceFile sourceFile = scanFile("/metrics/comments_none.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES)).isEqualTo(0);

    assertThat(sourceFile.getNoSonarTagLines().size()).isEqualTo(0);
  }

  @Test
  public void comments() {
    SourceFile sourceFile = scanFile("/metrics/comments.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES)).isEqualTo(3);

    assertThat(sourceFile.getNoSonarTagLines().size()).isEqualTo(2);
    assertThat(sourceFile.getNoSonarTagLines()).containsOnly(5, 6);
  }

  @Test
  public void headerComments() {
    SourceFile sourceFile = scanFileIgnoreHeaderComments("/metrics/header_comments.mc");

    assertThat(sourceFile.getInt(MiniCMetrics.COMMENT_LINES)).isEqualTo(1);
    assertThat(sourceFile.getNoSonarTagLines().size()).isEqualTo(0);
  }

}
