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
package org.sonar.squidbridge;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.test.minic.MiniCParser;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.squidbridge.api.SourceProject;
import org.sonar.squidbridge.test.miniC.MiniCAstScanner.MiniCMetrics;

import java.io.File;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProgressAstScannerTest {

  @Test
  public void test() throws Exception {
    SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(new SourceProject(""));
    Parser<Grammar> parser = MiniCParser.create();
    AstScanner<Grammar> scanner = new ProgressAstScanner.Builder<Grammar>(context)
      .setBaseParser(parser)
      .setFilesMetric(MiniCMetrics.FILES)
      .build();

    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    @SuppressWarnings("unchecked")
    Appender<LoggingEvent> mockAppender = mock(Appender.class);
    root.addAppender(mockAppender);

    scanner.scanFile(new File("src/test/resources/metrics/lines.mc"));

    verify(mockAppender).doAppend(argThat(new ArgumentMatcher<LoggingEvent>() {
      @Override
      public boolean matches(final Object argument) {
        return ((LoggingEvent) argument).getFormattedMessage().contains("1 source files to be analyzed");
      }
    }));
  }

}
