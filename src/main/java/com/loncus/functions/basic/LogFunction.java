package com.loncus.functions.basic;

import com.loncus.Expression;
import com.loncus.data.EvaluationValue;
import com.loncus.functions.AbstractFunction;
import com.loncus.functions.FunctionParameter;
import com.loncus.parser.Token;

/** The natural logarithm (base e) of a value */
@FunctionParameter(name = "value", nonZero = true, nonNegative = true)
public class LogFunction extends AbstractFunction {
  @Override
  public EvaluationValue evaluate(
      Expression expression, Token functionToken, EvaluationValue... parameterValues) {

    double d = parameterValues[0].getNumberValue().doubleValue();

    return expression.convertDoubleValue(Math.log(d));
  }
}
