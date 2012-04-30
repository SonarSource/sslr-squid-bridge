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
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.squid.*;
import com.sonar.sslr.squid.metrics.*;
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

  public static AstScanner<MiniCGrammar> create(SquidAstVisitor<MiniCGrammar>... visitors) {
    return create(false, visitors);
  }

  public static AstScanner<MiniCGrammar> createIgnoreHeaderComments(SquidAstVisitor<MiniCGrammar>... visitors) {
    return create(true, visitors);
  }

  private static AstScanner<MiniCGrammar> create(boolean ignoreHeaderComments, SquidAstVisitor<MiniCGrammar>... visitors) {

    final SquidAstVisitorContextImpl<MiniCGrammar> context = new SquidAstVisitorContextImpl<MiniCGrammar>(
        new SourceProject("MiniC Project"));
    final Parser<MiniCGrammar> parser = MiniCParser.create();

    AstScanner.Builder<MiniCGrammar> builder = AstScanner.<MiniCGrammar> builder(context).setBaseParser(parser);

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
    builder.withSquidAstVisitor(new SourceCodeBuilderVisitor<MiniCGrammar>(new SourceCodeBuilderCallback() {

      public SourceCode createSourceCode(SourceCode parentSourceCode, AstNode astNode) {
        String functionName = astNode.findFirstChild(parser.getGrammar().binFunctionDefinition).getTokenValue();

        SourceFunction function = new SourceFunction(astNode.getFromIndex() + "@" + functionName);
        function.setStartAtLine(astNode.getTokenLine());

        return function;
      }
    }, parser.getGrammar().functionDefinition));

    builder.withSquidAstVisitor(CounterVisitor.<MiniCGrammar> builder().setMetricDef(MiniCMetrics.FUNCTIONS)
        .subscribeTo(parser.getGrammar().functionDefinition).build());

    /* Metrics */
    builder.withSquidAstVisitor(new LinesVisitor<MiniCGrammar>(MiniCMetrics.LINES));
    builder.withSquidAstVisitor(new LinesOfCodeVisitor<MiniCGrammar>(MiniCMetrics.LINES_OF_CODE));
    builder.withSquidAstVisitor(CommentsVisitor.<MiniCGrammar> builder().withCommentMetric(MiniCMetrics.COMMENT_LINES)
        .withBlankCommentMetric(MiniCMetrics.BLANK_COMMENT_LINES)
        .withNoSonar(true)
        .withIgnoreHeaderComment(ignoreHeaderComments)
        .build());
    builder.withSquidAstVisitor(CounterVisitor.<MiniCGrammar> builder().setMetricDef(MiniCMetrics.STATEMENTS)
        .subscribeTo(parser.getGrammar().statement).build());

    AstNodeType[] complexityAstNodeType = new AstNodeType[] {
      parser.getGrammar().functionDefinition,
      parser.getGrammar().returnStatement,
      parser.getGrammar().ifStatement,
      parser.getGrammar().whileStatement,
      parser.getGrammar().continueStatement,
      parser.getGrammar().breakStatement
    };
    builder.withSquidAstVisitor(ComplexityVisitor.<MiniCGrammar> builder().setMetricDef(MiniCMetrics.COMPLEXITY)
        .subscribeTo(complexityAstNodeType).addExclusions(parser.getGrammar().noComplexityStatement).build());

    /* External visitors (typically Check ones) */
    for (SquidAstVisitor<MiniCGrammar> visitor : visitors) {
      builder.withSquidAstVisitor(visitor);
    }

    return builder.build();
  }
}
