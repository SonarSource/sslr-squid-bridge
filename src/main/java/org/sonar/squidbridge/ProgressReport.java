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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class ProgressReport implements Runnable {

  private final long period;
  private final Logger logger;
  private int count;
  private int currentFileNumber = -1;
  private File currentFile;
  private Iterator<File> it;
  private final Thread thread;
  private final String adjective;

  public ProgressReport(String threadName, long period, Logger logger, String adjective) {
    this.period = period;
    this.logger = logger;
    this.adjective = adjective;
    thread = new Thread(this);
    thread.setName(threadName);
  }

  public ProgressReport(String threadName, long period, String adjective) {
    this(threadName, period, LoggerFactory.getLogger(ProgressReport.class), adjective);
  }

  public ProgressReport(String threadName, long period) {
    this(threadName, period, "analyzed");
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        Thread.sleep(period);
        synchronized (this) {
          log(currentFileNumber + "/" + count + " files " + adjective + ", current file: " + currentFile.getAbsolutePath());
        }
      } catch (InterruptedException e) {
        thread.interrupt();
      }
    }
    synchronized (this) {
      log(count + "/" + count + " source files have been " + adjective);
    }
  }

  public synchronized void start(Collection<File> files) {
    count = files.size();
    it = files.iterator();

    nextFile();

    log(count + " source files to be " + adjective);
    thread.start();
  }

  public synchronized void nextFile() {
    if (it.hasNext()) {
      currentFileNumber++;
      currentFile = it.next();
    }
  }

  public synchronized void stop() {
    thread.interrupt();
  }

  public void join() throws InterruptedException {
    thread.join();
  }

  private void log(String message) {
    synchronized (logger) {
      logger.info(message);
      logger.notifyAll();
    }
  }

}
