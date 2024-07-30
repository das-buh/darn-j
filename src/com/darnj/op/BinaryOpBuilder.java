package com.darnj.op;

import com.darnj.Span;

public final class BinaryOpBuilder {
    BinaryOp op;

    public BinaryOpBuilder(BinaryOp op) {
        this.op = op;
    }

    public BinaryOp build(Span pos, Op lhs, Op rhs) {
        op.pos = pos;
        op.lhs = lhs;
        op.rhs = rhs;
        var out = op;
        op = null;
        return out;
    }
}
