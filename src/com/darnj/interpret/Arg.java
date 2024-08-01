package com.darnj.interpret;

import com.darnj.Span;
import com.darnj.value.*;

public record Arg(Value value, Span pos) {}
