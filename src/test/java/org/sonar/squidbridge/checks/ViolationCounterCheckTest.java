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

import org.sonar.squidbridge.checks.AbstractParseErrorCheck;
import org.sonar.squidbridge.checks.ViolationCounterCheck;
import com.sonar.sslr.api.Grammar;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.sonar.squidbridge.metrics.ResourceParser.scanFile;

import static org.fest.assertions.Assertions.assertThat;

public class ViolationCounterCheckTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void test() throws Exception {
    String projectsDir = FileUtils.toFile(ResourceParser.class.getResource("/checks/parse_error.mc")).getParentFile().getAbsolutePath();

    File output = temporaryFolder.newFile();

    ViolationCounterCheck.ViolationCounter counter = new ViolationCounterCheck.ViolationCounter();
    ViolationCounterCheck<Grammar> violationCounterCheck = new ViolationCounterCheck<Grammar>(projectsDir, counter);

    AbstractParseErrorCheck<Grammar> parseErrorCheck = new AbstractParseErrorCheck<Grammar>() {};

    scanFile("/checks/parse_error.mc", parseErrorCheck, violationCounterCheck);

    counter.saveToFile(output.getAbsolutePath());
    assertThat(output).isFile();

    ViolationCounterCheck.ViolationDifferenceAnalyzer analyzer = new ViolationCounterCheck.ViolationDifferenceAnalyzer(
      new ViolationCounterCheck.ViolationCounter(),
      ViolationCounterCheck.ViolationCounter.loadFromFile(output));
    analyzer.printReport();
    assertThat(analyzer.hasDifferences()).isTrue();

    analyzer = new ViolationCounterCheck.ViolationDifferenceAnalyzer(
      ViolationCounterCheck.ViolationCounter.loadFromFile(output),
      new ViolationCounterCheck.ViolationCounter());
    analyzer.printReport();
    assertThat(analyzer.hasDifferences()).isTrue();

    analyzer = new ViolationCounterCheck.ViolationDifferenceAnalyzer(
      ViolationCounterCheck.ViolationCounter.loadFromFile(output),
      ViolationCounterCheck.ViolationCounter.loadFromFile(output));
    analyzer.printReport();
    assertThat(analyzer.hasDifferences()).isFalse();
  }

}
