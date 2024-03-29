package com.loncus.config;

import com.loncus.data.*;
import com.loncus.data.conversion.DefaultEvaluationValueConverter;
import com.loncus.data.conversion.EvaluationValueConverterIfc;
import com.loncus.functions.FunctionIfc;
import com.loncus.functions.basic.*;
import com.loncus.functions.timeseries.*;
import com.loncus.operators.OperatorIfc;
import com.loncus.operators.arithmetic.*;
import com.loncus.operators.booleans.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;

/**
 * The expression configuration can be used to configure various aspects of expression parsing and
 * evaluation. <br>
 * A <code>Builder</code> is provided to create custom configurations, e.g.: <br>
 *
 * <pre>
 *   ExpressionConfiguration config = ExpressionConfiguration.builder().mathContext(MathContext.DECIMAL32).arraysAllowed(false).build();
 * </pre>
 *
 * <br>
 * Additional operators and functions can be added to an existing configuration:<br>
 *
 * <pre>
 *     ExpressionConfiguration.defaultConfiguration()
 *        .withAdditionalOperators(
 *            new AbstractMap.SimpleEntry<>("++", new PrefixPlusPlusOperator()),
 *            new AbstractMap.SimpleEntry<>("++", new PostfixPlusPlusOperator()))
 *        .withAdditionalFunctions(new AbstractMap.SimpleEntry<>("save", new SaveFunction()),
 *            new AbstractMap.SimpleEntry<>("update", new UpdateFunction()));
 * </pre>
 */
@Builder(toBuilder = true)
public class ExpressionConfiguration {

  /** The standard set constants for EvalEx. */
  public static final Map<String, EvaluationValue> StandardConstants =
      Collections.unmodifiableMap(getStandardConstants());

  /** Setting the decimal places to unlimited, will disable intermediate rounding. */
  public static final int DECIMAL_PLACES_ROUNDING_UNLIMITED = -1;

  /** The default math context has a precision of 68 and {@link RoundingMode#HALF_EVEN}. */
  public static final MathContext DEFAULT_MATH_CONTEXT =
      new MathContext(68, RoundingMode.HALF_EVEN);

  /** The default zone id is the systemd default zone ID. */
  public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

  
  
  /** The operator dictionary holds all operators that will be allowed in an expression. */
  @Builder.Default
  @Getter
  @SuppressWarnings("unchecked")
  private final OperatorDictionaryIfc operatorDictionary =
      MapBasedOperatorDictionary.ofOperators(
          // arithmetic
          new AbstractMap.SimpleEntry<>("+", new PrefixPlusOperator()), 
          new AbstractMap.SimpleEntry<>("-", new PrefixMinusOperator()), 
          new AbstractMap.SimpleEntry<>("+", new InfixPlusOperator()),
          new AbstractMap.SimpleEntry<>("-", new InfixMinusOperator()),
          new AbstractMap.SimpleEntry<>("*", new InfixMultiplicationOperator()),
          new AbstractMap.SimpleEntry<>("/", new InfixDivisionOperator()),
          new AbstractMap.SimpleEntry<>("^", new InfixPowerOfOperator()),
          new AbstractMap.SimpleEntry<>("%", new InfixModuloOperator()),
          // booleans
          new AbstractMap.SimpleEntry<>("=", new InfixEqualsOperator()),
          new AbstractMap.SimpleEntry<>("==", new InfixEqualsOperator()),
          new AbstractMap.SimpleEntry<>("!=", new InfixNotEqualsOperator()),
          new AbstractMap.SimpleEntry<>("<>", new InfixNotEqualsOperator()),
          new AbstractMap.SimpleEntry<>(">", new InfixGreaterOperator()),
          new AbstractMap.SimpleEntry<>(">=", new InfixGreaterEqualsOperator()),
          new AbstractMap.SimpleEntry<>("<", new InfixLessOperator()),
          new AbstractMap.SimpleEntry<>("<=", new InfixLessEqualsOperator()),
          new AbstractMap.SimpleEntry<>("&&", new InfixAndOperator()),
          new AbstractMap.SimpleEntry<>("||", new InfixOrOperator()),
          new AbstractMap.SimpleEntry<>("!", new PrefixNotOperator()));

  /** The function dictionary holds all functions that will be allowed in an expression. */
  @Builder.Default
  @Getter
  @SuppressWarnings("unchecked")
  private final FunctionDictionaryIfc functionDictionary =
      MapBasedFunctionDictionary.ofFunctions(
          // basic functions
          new AbstractMap.SimpleEntry<>("ABS", new AbsFunction()),
          new AbstractMap.SimpleEntry<>("CEILING", new CeilingFunction()),
          new AbstractMap.SimpleEntry<>("FACT", new FactFunction()),
          new AbstractMap.SimpleEntry<>("FLOOR", new FloorFunction()),
          new AbstractMap.SimpleEntry<>("IF", new IfFunction()),
          new AbstractMap.SimpleEntry<>("LOG", new LogFunction()),
          new AbstractMap.SimpleEntry<>("LOG10", new Log10Function()),
          new AbstractMap.SimpleEntry<>("MAX", new MaxFunction()),
          new AbstractMap.SimpleEntry<>("MIN", new MinFunction()),
          new AbstractMap.SimpleEntry<>("NOT", new NotFunction()),
          new AbstractMap.SimpleEntry<>("SUM", new SumFunction()),
          new AbstractMap.SimpleEntry<>("SQRT", new SqrtFunction()),
          // TimeSeriesPoint functions
          new AbstractMap.SimpleEntry<>("MOVE", new MoveFunction()),
          new AbstractMap.SimpleEntry<>("MA", new MovingAvgFunction()));

  /** The math context to use. */
  @Builder.Default @Getter private final MathContext mathContext = DEFAULT_MATH_CONTEXT;

  /**
   * The data accessor is responsible for accessing variable and constant values in an expression.
   * The supplier will be called once for each new expression, the default is to create a new {@link
   * MapBasedDataAccessor} instance for each expression, providing a new storage for each
   * expression.
   */
  @Builder.Default @Getter
  private final Supplier<DataAccessorIfc> dataAccessorSupplier = MapBasedDataAccessor::new;

  /**
   * Default constants will be added automatically to each expression and can be used in expression
   * evaluation.
   */
  @Builder.Default @Getter
  private final Map<String, EvaluationValue> defaultConstants = getStandardConstants();

  /** Support for arrays in expressions are allowed or not. */
  @Builder.Default @Getter private final boolean arraysAllowed = true;

  /** Support for indicator variable in expressions are allowed or not. */
  @Builder.Default @Getter private final boolean varsAllowed = true;

  /** Support for implicit multiplication, like in (a+b)(b+c) are allowed or not. */
  @Builder.Default @Getter private final boolean implicitMultiplicationAllowed = true;

  /**
   * The power of operator precedence, can be set higher {@link
   * OperatorIfc#OPERATOR_PRECEDENCE_POWER_HIGHER} or to a custom value.
   */
  @Builder.Default @Getter
  private final int powerOfPrecedence = OperatorIfc.OPERATOR_PRECEDENCE_POWER;

  /**
   * If specified, all results from operations and functions will be rounded to the specified number
   * of decimal digits, using the MathContexts rounding mode.
   */
  @Builder.Default @Getter
  private final int decimalPlacesRounding = DECIMAL_PLACES_ROUNDING_UNLIMITED;

  /**
   * If set to true (default), then the trailing decimal zeros in a number result will be stripped.
   */
  @Builder.Default @Getter private final boolean stripTrailingZeros = true;

  /**
   * If set to true (default), then variables can be set that have the name of a constant. In that
   * case, the constant value will be removed and a variable value will be set.
   */
  @Builder.Default @Getter private final boolean allowOverwriteConstants = true;

  /** The time zone id. By default, the system default zone id is used. */
  @Builder.Default @Getter private final ZoneId zoneId = DEFAULT_ZONE_ID;

  /** The converter to use when converting different data types to an {@link EvaluationValue}. */
  @Builder.Default @Getter
  private final EvaluationValueConverterIfc evaluationValueConverter =
      new DefaultEvaluationValueConverter();

  /**
   * Convenience method to create a default configuration.
   *
   * @return A configuration with default settings.
   */
  public static ExpressionConfiguration defaultConfiguration() {
    return ExpressionConfiguration.builder().build();
  }

  /**
   * Adds additional operators to this configuration.
   *
   * @param operators variable number of arguments with a map entry holding the operator name and
   *     implementation. <br>
   *     Example: <code>
   *        ExpressionConfiguration.defaultConfiguration()
   *          .withAdditionalOperators(
   *            new AbstractMap.SimpleEntry<>("++", new PrefixPlusPlusOperator()),
   *            new AbstractMap.SimpleEntry<>("++", new PostfixPlusPlusOperator()));
   *     </code>
   * @return The modified configuration, to allow chaining of methods.
   */
  @SafeVarargs
  public final ExpressionConfiguration withAdditionalOperators(
      Map.Entry<String, OperatorIfc>... operators) {
    Arrays.stream(operators)
        .forEach(entry -> operatorDictionary.addOperator(entry.getKey(), entry.getValue()));
    return this;
  }

  /**
   * Adds additional functions to this configuration.
   *
   * @param functions variable number of arguments with a map entry holding the functions name and
   *     implementation. <br>
   *     Example: <code>
   *        ExpressionConfiguration.defaultConfiguration()
   *          .withAdditionalFunctions(
   *            new AbstractMap.SimpleEntry<>("save", new SaveFunction()),
   *            new AbstractMap.SimpleEntry<>("update", new UpdateFunction()));
   *     </code>
   * @return The modified configuration, to allow chaining of methods.
   */
  @SafeVarargs
  public final ExpressionConfiguration withAdditionalFunctions(
      Map.Entry<String, FunctionIfc>... functions) {
    Arrays.stream(functions)
        .forEach(entry -> functionDictionary.addFunction(entry.getKey(), entry.getValue()));
    return this;
  }

  private static Map<String, EvaluationValue> getStandardConstants() {

    Map<String, EvaluationValue> constants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    constants.put("TRUE", EvaluationValue.booleanValue(true));
    constants.put("FALSE", EvaluationValue.booleanValue(false));
    constants.put(
        "PI",
        EvaluationValue.numberValue(
            new BigDecimal(
                "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")));
    constants.put(
        "E",
        EvaluationValue.numberValue(
            new BigDecimal(
                "2.71828182845904523536028747135266249775724709369995957496696762772407663")));
    constants.put("NULL", EvaluationValue.nullValue());

    return constants;
  }
}
