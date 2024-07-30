package com.darnj.op;

import com.darnj.Span;

public final class UnaryOpBuilder {
    UnaryOp op;

    public UnaryOpBuilder(UnaryOp op) {
        this.op = op;
    }

    public UnaryOp build(Span pos, Op operand) {
        op.pos = pos;
        op.operand = operand;
        var out = op;
        op = null;
        return out;
    }
}
