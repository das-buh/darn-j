package com.darnj.op;

import com.darnj.Span;

public abstract class UnaryOp extends Op {
    Op operand;

    public UnaryOp build(Span pos, Op operand) {
        this.pos = pos;
        this.operand = operand;
        return this;
    }
}
