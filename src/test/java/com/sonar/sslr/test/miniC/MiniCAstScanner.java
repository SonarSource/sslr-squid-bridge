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
package com.sonar.sslr.test.miniC;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.CommentAnalyser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.squid.AstScanner;
import com.sonar.sslr.squid.SourceCodeBuilderCallback;
import com.sonar.sslr.squid.SourceCodeBuilderVisitor;
import com.sonar.sslr.squid.SquidAstVisitor;
import com.sonar.sslr.squid.SquidAstVisitorContextImpl;
import com.sonar.sslr.squid.metrics.CommentsVisitor;
import com.sonar.sslr.squid.metrics.ComplexityVisitor;
import com.sonar.sslr.squid.metrics.CounterVisitor;
import com.sonar.sslr.squid.metrics.LinesOfCodeVisitor;
import com.sonar.sslr.squid.metrics.LinesVisitor;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceFunction;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.measures.AggregationFormula;
import org.sonar.squid.measures.CalculatedMetricFormula;
import org.sonar.squid.measures.MetricDef;
import org.sonar.squid.measures.SumAggregationFormula;

public final class MiniCAstScanner {

  public static enum MiniCMetrics implements MetricDef {
    FILES, STATEMENTS, COMPLEXITY, LINES, LINES_OF_CODE, COMMENT_LINES, BLANK_COMMENT_LINES, FUNCTIONS;

    public double getInitValue() {
      return 0;
    }

    public String getName() {
      return name();
    }

    public boolean isCalculatedMetric() {
      return false;
    }

    public boolean aggregateIfThereIsAlreadyAValue() {
      return true;
    }

    public boolean isThereAggregationFormula() {
      return true;
    }

    public CalculatedMetricFormula getCalculatedMetricFormula() {
      return null;
    }

    public AggregationFormula getAggregationFormula() {
      return new SumAggregationFormula();
    }

  }

  private MiniCAstScanner() {
  }

  public static AstScanner<Grammar> create(SquidAstVisitor<Grammar>... visitors) {
    return create(false, visitors);
  }

  public static AstScanner<Grammar> createIgnoreHeaderComments(SquidAstVisitor<Grammar>... visitors) {
    return create(true, visitors);
  }

  private static AstScanner<Grammar> create(boolean ignoreHeaderComments, SquidAstVisitor<Grammar>... visitors) {

    final SquidAstVisitorContextImpl<Grammar> context = new SquidAstVisitorContextImpl<Grammar>(
        new SourceProject("MiniC Project"));
    final Parser<Grammar> parser = MiniCParser.create();

    AstScanner.Builder<Grammar> builder = AstScanner.<Grammar> builder(context).setBaseParser(parser);

    /* Metrics */
    builder.withMetrics(MiniCMetrics.values());

    /* Comments */
    builder.setCommentAnalyser(
        new CommentAnalyser() {

          @Override
          public boolean isBlank(String commentLine) {
            for (int i = 0; i < commentLine.length(); i++) {
              if (Character.isLetterOrDigit(commentLine.charAt(i))) {
                return false;
              }
            }

            return true;
          }

          @Override
          public String getContents(String comment) {
            return comment.substring(2, comment.length() - 2);
          }

        }
        );

    /* Files */
    builder.setFilesMetric(MiniCMetrics.FILES);

    /* Functions */
    builder.withSquidAstVisitor(new SourceCodeBuilderVisitor<Grammar>(new SourceCodeBuilderCallback() {

      public SourceCode createSourceCode(SourceCode parentSourceCode, AstNode astNode) {
        String functionName = astNode.findFirstChild(MiniCGrammar.BIN_FUNCTION_DEFINITION).getTokenValue();

        SourceFunction function = new SourceFunction(astNode.getFromIndex() + "@" + functionName);
        function.setStartAtLine(astNode.getTokenLine());

        return function;
      }
    }, MiniCGrammar.FUNCTION_DEFINITION));

    builder.withSquidAstVisitor(CounterVisitor.<Grammar> builder().setMetricDef(MiniCMetrics.FUNCTIONS)
        .subscribeTo(MiniCGrammar.FUNCTION_DEFINITION).build());

    /* Metrics */
    builder.withSquidAstVisitor(new LinesVisitor<Grammar>(MiniCMetrics.LINES));
    builder.withSquidAstVisitor(new LinesOfCodeVisitor<Grammar>(MiniCMetrics.LINES_OF_CODE));
    builder.withSquidAstVisitor(CommentsVisitor.<Grammar> builder().withCommentMetric(MiniCMetrics.COMMENT_LINES)
        .withBlankCommentMetric(MiniCMetrics.BLANK_COMMENT_LINES)
        .withNoSonar(true)
        .withIgnoreHeaderComment(ignoreHeaderComments)
        .build());
    builder.withSquidAstVisitor(CounterVisitor.<Grammar> builder().setMetricDef(MiniCMetrics.STATEMENTS)
        .subscribeTo(MiniCGrammar.STATEMENT).build());

    AstNodeType[] complexityAstNodeType = new AstNodeType[] {
      MiniCGrammar.FUNCTION_DEFINITION,
      MiniCGrammar.RETURN_STATEMENT,
      MiniCGrammar.IF_STATEMENT,
      MiniCGrammar.WHILE_STATEMENT,
      MiniCGrammar.CONTINUE_STATEMENT,
      MiniCGrammar.BREAK_STATEMENT
    };
    builder.withSquidAstVisitor(ComplexityVisitor.<Grammar> builder().setMetricDef(MiniCMetrics.COMPLEXITY)
        .subscribeTo(complexityAstNodeType).addExclusions(MiniCGrammar.NO_COMPLEXITY_STATEMENT).build());

    /* External visitors (typically Check ones) */
    for (SquidAstVisitor<Grammar> visitor : visitors) {
      builder.withSquidAstVisitor(visitor);
    }

    return builder.build();
  }

}
