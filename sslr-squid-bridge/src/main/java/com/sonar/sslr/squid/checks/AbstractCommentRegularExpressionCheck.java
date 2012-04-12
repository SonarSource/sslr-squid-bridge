/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.squid.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.utils.SonarException;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.*;

public abstract class AbstractCommentRegularExpressionCheck<GRAMMAR extends Grammar> extends SquidCheck<GRAMMAR> implements AstAndTokenVisitor {

  // See SONAR-3164
  public abstract String getRegularExpression();

  // See SONAR-3164
  public abstract String getMessage();

  private Pattern pattern = null;

  @Override
  public void init() {
    String regularExpression = getRegularExpression();
    checkNotNull(regularExpression, "getRegularExpression() should not return null");

    if (!"".equals(regularExpression)) {
      try {
        pattern = Pattern.compile(regularExpression);
      } catch (RuntimeException e) {
        throw new SonarException("[AbstractCommentRegularExpressionCheck] Unable to compile the regular expression ("
          + regularExpression
          + " given).", e);
      }
    }
  }

  public void visitToken(Token token) {
    if (pattern != null) {
      for (Trivia trivia : token.getTrivia()) {
        if (trivia.isComment() && pattern.matcher(trivia.getToken().getOriginalValue()).matches()) {
          getContext().createLineViolation(this, getMessage(), trivia.getToken());
        }
      }
    }
  }

}
