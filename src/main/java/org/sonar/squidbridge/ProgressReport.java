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

public class ProgressReport implements Runnable {

  private final long period;
  private final Logger logger;
  private int count;
  private int currentFileNumber = 0;
  private final Thread thread;
  private String message;

  public ProgressReport(String threadName, long period, Logger logger) {
    this.period = period;
    this.logger = logger;
    thread = new Thread(this);
    thread.setName(threadName);
  }

  public ProgressReport(String threadName, long period) {
    this(threadName, period, LoggerFactory.getLogger(ProgressReport.class));
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        Thread.sleep(period);
        synchronized (this) {
          log(message);
        }
      } catch (InterruptedException e) {
        thread.interrupt();
      }
    }

    synchronized (this) {
      log(currentFileNumber + "/" + count + " source files have been analyzed");
    }
  }

  public synchronized void start(int count) {
    this.count = count;

    log(count + " source files to be analyzed");
    message = "Files analysis did not start yet";

    thread.start();
  }

  public synchronized void setFile(File file) {
    message = currentFileNumber + "/" + count + " files analyzed so far, currently analyzing: " + file.getAbsolutePath();
    currentFileNumber++;
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
