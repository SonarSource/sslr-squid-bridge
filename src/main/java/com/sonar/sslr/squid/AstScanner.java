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

import com.sonar.sslr.api.*;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.impl.ast.AstWalker;
import com.sonar.sslr.impl.events.ExtendedStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.squid.api.AnalysisException;
import org.sonar.squid.api.SourceCodeSearchEngine;
import org.sonar.squid.api.SourceCodeTreeDecorator;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.indexer.SquidIndex;
import org.sonar.squid.measures.MetricDef;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class AstScanner<G extends Grammar> {

  private static final Logger LOG = LoggerFactory.getLogger(AstScanner.class);
  private final SquidAstVisitorContextImpl<G> context;
  private final ParserRecoveryListener parserRecoveryListener;
  private final Parser<G> parserProduction;
  private final Parser<G> parserDebug;
  private final List<SquidAstVisitor<G>> visitors;
  private final AuditListener[] auditListeners;
  private final SquidIndex indexer = new SquidIndex();
  private final CommentAnalyser commentAnalyser;
  private final MetricDef[] metrics;
  private final MetricDef filesMetric;

  private AstScanner(Builder<G> builder) {
    this.visitors = new ArrayList<SquidAstVisitor<G>>(builder.visitors);
    this.auditListeners = builder.auditListeners.toArray(new AuditListener[builder.auditListeners.size()]);

    this.parserRecoveryListener = new ParserRecoveryListener();
    this.parserProduction = Parser.builder(builder.baseParser).setRecognictionExceptionListener(parserRecoveryListener).build();

    this.commentAnalyser = builder.commentAnalyser;
    this.context = builder.context;
    this.context.setGrammar(parserProduction.getGrammar());
    this.context.getProject().setSourceCodeIndexer(indexer);
    this.context.setCommentAnalyser(commentAnalyser);
    this.metrics = builder.metrics;
    this.filesMetric = builder.filesMetric;
    indexer.index(context.getProject());

    ParserRecoveryLogger parserRecoveryLogger = new ParserRecoveryLogger();
    parserRecoveryLogger.setContext(this.context);
    this.parserDebug = Parser.builder(builder.baseParser).setParsingEventListeners().setExtendedStackTrace(new ExtendedStackTrace())
        .setRecognictionExceptionListener(this.auditListeners).addRecognictionExceptionListeners(parserRecoveryLogger).build();
  }

  public SourceCodeSearchEngine getIndex() {
    return indexer;
  }

  public void scanFile(File plSqlFile) {
    scanFiles(Arrays.asList(plSqlFile));
  }

  public void scanFiles(Collection<File> files) {
    for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
      visitor.init();
    }

    for (File file : files) {
      try {
        context.setFile(file, filesMetric);
        parserRecoveryListener.reset();

        AstNode ast = parserProduction.parse(file);

        // Process the parsing recoveries
        if (parserRecoveryListener.didRecover()) {
          try {
            parserDebug.parse(file);
          } catch (Exception e) {
            LOG.error("Unable to get an extended stack trace on file : " + file.getAbsolutePath(), e);
            LOG.error("Parsing error recoveries not shown.");
          }
        }

        AstWalker astWalker = new AstWalker(visitors);
        astWalker.walkAndVisit(ast);

        context.setFile(null, null);
        astWalker = null;
      } catch (RecognitionException e) {
        LOG.error("Unable to parse source file : " + file.getAbsolutePath());

        try {
          if (e.isToRetryWithExtendStackTrace()) {
            try {
              parserDebug.parse(file);
            } catch (RecognitionException re) {
              e = re;
            } catch (Exception e2) {
              LOG.error("Unable to get an extended stack trace on file : " + file.getAbsolutePath(), e2);
            }

            // Log the recognition exception
            LOG.error(e.getMessage());
          } else {
            LOG.error(e.getMessage(), e);
          }

          // Process the exception
          for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
            visitor.visitFile(null);
          }

          for (AuditListener auditListener : auditListeners) {
            auditListener.processRecognitionException(e);
          }

          for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
            visitor.leaveFile(null);
          }
        } catch (Exception e2) {
          String errorMessage = "Sonar is unable to analyze file : '" + file.getAbsolutePath() + "'";
          throw new AnalysisException(errorMessage, e);
        }
      } catch (Exception e) {
        String errorMessage = "Sonar is unable to analyze file : '" + file.getAbsolutePath() + "'";
        throw new AnalysisException(errorMessage, e);
      }
    }

    for (SquidAstVisitor<? extends Grammar> visitor : visitors) {
      visitor.destroy();
    }

    decorateSquidTree();
  }

  private void decorateSquidTree() {
    if (metrics != null && metrics.length > 0) {
      SourceProject project = context.getProject();
      SourceCodeTreeDecorator decorator = new SourceCodeTreeDecorator(project);
      decorator.decorateWith(metrics);
    }
  }

  public static <G extends Grammar> Builder<G> builder(SquidAstVisitorContextImpl<G> context) {
    return new Builder<G>(context);
  }

  public static class Builder<G extends Grammar> {

    private Parser<G> baseParser;
    private final List<SquidAstVisitor<G>> visitors = new ArrayList<SquidAstVisitor<G>>();
    private final List<AuditListener> auditListeners = new ArrayList<AuditListener>();
    private final SquidAstVisitorContextImpl<G> context;
    private CommentAnalyser commentAnalyser;
    private MetricDef[] metrics;
    private MetricDef filesMetric;

    public Builder(SquidAstVisitorContextImpl<G> context) {
      checkNotNull(context, "context cannot be null");
      this.context = context;
    }

    public Builder<G> setBaseParser(Parser<G> baseParser) {
      checkNotNull(baseParser, "baseParser cannot be null");
      this.baseParser = baseParser;
      return this;
    }

    public Builder<G> setCommentAnalyser(CommentAnalyser commentAnalyser) {
      checkNotNull(commentAnalyser, "commentAnalyser cannot be null");
      this.commentAnalyser = commentAnalyser;
      return this;
    }

    public Builder<G> withSquidAstVisitor(SquidAstVisitor<G> visitor) {
      checkNotNull(visitor, "visitor cannot be null");

      visitor.setContext(context);

      if (visitor instanceof AuditListener) {
        auditListeners.add((AuditListener) visitor);
      }

      visitors.add(visitor);
      return this;
    }

    public Builder<G> withMetrics(MetricDef... metrics) {
      for (MetricDef metric : metrics) {
        checkNotNull(metric, "metrics cannot be null");
      }
      this.metrics = metrics;
      return this;
    }

    public Builder<G> setFilesMetric(MetricDef filesMetric) {
      checkNotNull(filesMetric, "filesMetric cannot be null");
      this.filesMetric = filesMetric;
      return this;
    }

    public AstScanner<G> build() {
      checkState(baseParser != null, "baseParser must be set");
      checkState(commentAnalyser != null, "commentAnalyser must be set");
      checkState(filesMetric != null, "filesMetric must be set");

      return new AstScanner<G>(this);
    }
  }

  private class ParserRecoveryListener implements AuditListener {

    private int recovers = 0;

    public void processRecognitionException(RecognitionException re) {
      if (re.isFatal()) {
        throw new IllegalStateException(
            "ParserRecoveryListener.processRecognitionException() is not supposed to be called with fatal recognition exceptions.", re);
      }

      recovers++;
    }

    public void processException(Exception e) {
      throw new IllegalStateException("ParserRecoveryListener.processException() is not supposed to be called in recovery mode.", e);
    }

    public boolean didRecover() {
      return recovers > 0;
    }

    public void reset() {
      recovers = 0;
    }

  }

  private class ParserRecoveryLogger extends SquidAstVisitor<G> implements AuditListener {

    public void processRecognitionException(RecognitionException re) {
      if (re.isFatal()) {
        throw new IllegalStateException(
            "ParserRecoveryLogger.processRecognitionException() is not supposed to be called with fatal recognition exceptions.", re);
      }

      LOG.warn("Unable to completely parse the file " + getContext().getFile().getAbsolutePath());
      LOG.warn(re.getMessage());
    }

    public void processException(Exception e) {
      throw new IllegalStateException("ParserRecoveryLogger.processException() is not supposed to be called in recovery mode.", e);
    }

  }

}
