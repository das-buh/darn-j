package com.darnj.interpret;

import com.darnj.Span;
import com.darnj.type.*;
import com.darnj.value.*;

public record Arg(Value value, Span pos) {
    public Type type() {
        return value.type();
    }
}
