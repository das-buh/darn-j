package com.darnj.op;

import com.darnj.Span;
import com.darnj.value.*;

public class ReturnEffect extends ControlFlowEffect {
    Value value;

    ReturnEffect(Span pos, Value value) {
        super(pos);
        this.value = value;
    }

    public Value value() {
        return value;
    }
}
