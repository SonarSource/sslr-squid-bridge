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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgressReportTest {

  @Rule
  public final Timeout timeout = new Timeout(5000);

  @Test
  public void test() throws Exception {
    Logger logger = mock(Logger.class);
    ProgressReport report = new ProgressReport(ProgressReport.class.getName(), 100, logger);

    File file = mock(File.class);
    when(file.getAbsolutePath()).thenReturn("foo");
    report.start(2);
    report.setFile(file);

    // Wait for start message
    waitForMessage(logger);

    // Wait for at least one progress message
    waitForMessage(logger);

    report.setFile(file);

    // Wait for at least one progress message
    waitForMessage(logger);

    report.stop();

    // Waits for the thread to die
    // Note: We cannot simply wait for a message here, because it could either be a progress or a stop one
    report.join();

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(logger, atLeast(3)).info(captor.capture());

    List<String> messages = captor.getAllValues();
    assertThat(messages.size()).isGreaterThanOrEqualTo(3);
    assertThat(messages.get(0)).isEqualTo("2 source files to be analyzed");
    boolean foundFirst = false;
    boolean foundSecond = false;
    for (int i = 1; i < messages.size() - 1; i++) {
      String message = messages.get(i);

      if ("Files analysis did not start yet".equals(message)) {
        // Nothing
      } else if ("0/2 files analyzed so far, currently analyzing: foo".equals(message)) {
        foundFirst = true;
      } else if ("1/2 files analyzed so far, currently analyzing: foo".equals(message)) {
        foundSecond = true;
      }
    }

    assertThat(foundFirst).isTrue();
    assertThat(foundSecond).isTrue();

    assertThat(messages.get(messages.size() - 2)).isEqualTo("1/2 files analyzed so far, currently analyzing: foo");
    assertThat(messages.get(messages.size() - 1)).isEqualTo("2/2" + " source files have been analyzed");
  }

  @Test
  public void test_without_files() throws Exception {
    Logger logger = mock(Logger.class);
    ProgressReport report = new ProgressReport(ProgressReport.class.getName(), 100, logger);

    report.start(0);

    // Wait for start message
    waitForMessage(logger);

    // Wait for at least one progress message
    waitForMessage(logger);

    report.stop();

    // Waits for the thread to die
    // Note: We cannot simply wait for a message here, because it could either be a progress or a stop one
    report.join();

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(logger, atLeast(3)).info(captor.capture());

    List<String> messages = captor.getAllValues();
    assertThat(messages.size()).isGreaterThanOrEqualTo(3);
    assertThat(messages.get(0)).isEqualTo("0 source files to be analyzed");
    for (int i = 1; i < messages.size() - 1; i++) {
      assertThat(messages.get(i)).isEqualTo("Files analysis did not start yet");
    }
    assertThat(messages.get(messages.size() - 1)).isEqualTo("0/0" + " source files have been analyzed");
  }

  private static void waitForMessage(Logger logger) throws InterruptedException {
    synchronized (logger) {
      logger.wait();
    }
  }

}
