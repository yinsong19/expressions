package com.loncus.functions;

import java.lang.annotation.*;

/** Collator for repeatable {@link FunctionParameter} annotations. */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionParameters {
  FunctionParameter[] value();
}
