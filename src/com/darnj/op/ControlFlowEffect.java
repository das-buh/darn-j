package com.darnj.op;

import com.darnj.Span;

public abstract class ControlFlowEffect extends RuntimeException {
    Span pos;

    ControlFlowEffect(Span pos) {
        this.pos = pos;
    }

    public Span pos() {
        return pos;
    }
}
