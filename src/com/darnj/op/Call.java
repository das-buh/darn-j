package com.darnj.op;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Call extends Op {
    Span calleePos;
    int func;
    ArrayList<Op> args;

    public Call(Span pos, Span calleePos, int func, ArrayList<Op> args) {
        this.pos = pos;
        this.calleePos = calleePos;
        this.func = func;
        this.args = args;
    }

    @Override
    public Value eval(Context ctx) {
        if (ctx.funcs().containsKey(func)) {
            var func = ctx.funcs().get(this.func);
            ArrayList<Arg> args = this.args.stream()
                .map(op -> new Arg(op.eval(ctx), op.pos()))
                .collect(Collectors.toCollection(ArrayList::new));
            var callee = new Callee(this.func, calleePos, args, ctx);
            return func.eval(callee);
        }

        throw error("function `%s` is undefined", ctx.symbols().resolve(func));
    }
}