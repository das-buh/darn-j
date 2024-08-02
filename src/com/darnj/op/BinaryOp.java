package com.darnj.op;

import com.darnj.Span;

public abstract class BinaryOp extends Op {
    Op lhs;
    Op rhs;

    public BinaryOp build(Span pos, Op lhs, Op rhs) {
        this.pos = pos;
        this.lhs = lhs;
        this.rhs = rhs;
        return this;
    }
}
