package com.darnj.interpret;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.value.*;

public record Callee(int func, Span pos, ArrayList<Arg> args, Context ctx) {
    public String name() {
        return ctx.symbols().resolve(func);
    }

    public void arity(int arity) {
        if (args.size() != arity) {
            var format = "function `%s` takes %d arguments, but %d were supplied"; 
            throw new LangError(pos, String.format(format, name(), arity, args.size()));
        }
    }

    // Expects `args` and `params` to be the same length.
    public void types(ArrayList<Param> params) {
        var arity = params.size();
        for (var i = 0; i < arity; i++) {
            var arg = args.get(i);
            var param = params.get(i);
            if (!arg.value().type().eq(param.type())) {
                var format = "function `%s` expected argument type %s, but type %s was supplied"; 
                var paramType = param.type().name();
                var argType = arg.value().type().name();
                throw new LangError(arg.pos(), String.format(format, name(), paramType, argType));       
            }
        }
    }

    public LangError mismatch() {
        var format = "function `%s` is undefined for argument types (%s)";
        var types = args.stream()
            .map(arg -> arg.value().type().name())
            .collect(Collectors.joining(", "));
        return new LangError(pos, String.format(format, name(), types));
    }

    public Value arg(int idx) {
        var arg = args.get(idx);
        if (arg.value().inner instanceof UndefinedValue) {
            throw new LangError(arg.pos(), "argument cannot be type undefined");
        }
        return arg.value();
    }
}
