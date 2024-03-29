package com.loncus.config;

import com.loncus.functions.FunctionIfc;

/**
 * A function dictionary holds all the functions, that can be used in an expression. <br>
 * The default implementation is the {@link MapBasedFunctionDictionary}.
 */
public interface FunctionDictionaryIfc {

  /**
   * Allows to add a function to the dictionary. Implementation is optional, if you have a fixed set
   * of functions, this method can throw an exception.
   *
   * @param functionName The function name.
   * @param function The function implementation.
   */
  void addFunction(String functionName, FunctionIfc function);

  /**
   * Check if the dictionary has a function with that name.
   *
   * @param functionName The function name to look for.
   * @return <code>true</code> if a function was found or <code>false</code> if not.
   */
  default boolean hasFunction(String functionName) {
    return getFunction(functionName) != null;
  }

  /**
   * Get the function definition for a function name.
   *
   * @param functionName The name of the function.
   * @return The function definition or <code>null</code> if no function was found.
   */
  FunctionIfc getFunction(String functionName);
}
