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
package com.sonar.sslr.squid;

import java.io.File;
import java.util.Stack;

import org.sonar.squid.api.*;
import org.sonar.squid.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.CommentAnalyser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;

public final class SquidAstVisitorContextImpl<G extends Grammar> extends SquidAstVisitorContext<G> {

  private final Stack<SourceCode> sourceCodeStack = new Stack<SourceCode>();
  private G grammar;
  private File file;
  private final SourceProject project;
  private CommentAnalyser commentAnalyser;

  public SquidAstVisitorContextImpl(SourceProject project) {
    if (project == null) {
      throw new IllegalArgumentException("project cannot be null.");
    }

    this.project = project;
    sourceCodeStack.add(project);
  }

  public void setGrammar(G grammar) {
    this.grammar = grammar;
  }

  public void setCommentAnalyser(CommentAnalyser commentAnalyser) {
    this.commentAnalyser = commentAnalyser;
  }

  /** {@inheritDoc} */
  @Override
  public CommentAnalyser getCommentAnalyser() {
    return commentAnalyser;
  }

  /** {@inheritDoc} */
  @Override
  public void addSourceCode(SourceCode child) {
    peekSourceCode().addChild(child);
    sourceCodeStack.add(child);
  }

  /** {@inheritDoc} */
  @Override
  public void popSourceCode() {
    sourceCodeStack.pop();
  }

  /** {@inheritDoc} */
  @Override
  public SourceCode peekSourceCode() {
    return sourceCodeStack.peek();
  }

  public void setFile(File file, MetricDef filesMetric) {
    peekTillSourceProject();
    this.file = file;
    if (file != null) {
      SourceFile sourceFile = new SourceFile(file.getAbsolutePath(), file.getName());
      addSourceCode(sourceFile);
      peekSourceCode().setMeasure(filesMetric, 1);
    }
  }

  private void peekTillSourceProject() {
    while ( !(peekSourceCode() instanceof SourceProject)) {
      popSourceCode();
    }
  }

  /** {@inheritDoc} */
  @Override
  public File getFile() {
    return file;
  }

  public SourceProject getProject() {
    return project;
  }

  /** {@inheritDoc} */
  @Override
  public G getGrammar() {
    return grammar;
  }

  /** {@inheritDoc} */
  @Override
  public void createFileViolation(CodeCheck check, String message, Object... messageParameters) {
    createLineViolation(check, message, -1, messageParameters);
  }

  /** {@inheritDoc} */
  @Override
  public void createLineViolation(CodeCheck check, String message, AstNode node, Object... messageParameters) {
    createLineViolation(check, message, node.getToken(), messageParameters);
  }

  /** {@inheritDoc} */
  @Override
  public void createLineViolation(CodeCheck check, String message, Token token, Object... messageParameters) {
    createLineViolation(check, message, token.getLine(), messageParameters);
  }

  /** {@inheritDoc} */
  @Override
  public void createLineViolation(CodeCheck check, String message, int line, Object... messageParameters) {
    CheckMessage checkMessage = new CheckMessage(check, message, messageParameters);
    if (line > 0) {
      checkMessage.setLine(line);
    }
    log(checkMessage);
  }

  private void log(CheckMessage message) {
    if (peekSourceCode() instanceof SourceFile) {
      peekSourceCode().log(message);
    } else if (peekSourceCode().getParent(SourceFile.class) != null) {
      peekSourceCode().getParent(SourceFile.class).log(message);
    } else {
      throw new IllegalStateException("Unable to log a check message on source code '"
          + (peekSourceCode() == null ? "[NULL]" : peekSourceCode().getKey()) + "'");
    }
  }

}
