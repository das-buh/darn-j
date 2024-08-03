package com.darnj.interpret;

import com.darnj.Span;
import com.darnj.type.*;

public record Param(Span pos, int ident, Type type) {}
