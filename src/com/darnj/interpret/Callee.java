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
            throw new LangError(pos, format, name(), arity, args.size());
        }
    }

    public LangError mismatch() {
        var format = "function `%s` is undefined for argument types (%s)";
        var types = args.stream()
            .map(arg -> arg.type().name())
            .collect(Collectors.joining(", "));
        return new LangError(pos, format, name(), types);
    }

    public Arg arg(int idx) {
        return args.get(idx);
    }

    public long asInt(Arg arg) {
        if (arg.value().inner instanceof IntValue v) return v.value();
        else throw coerceError(arg, "int");
    }

    public double asFloat(Arg arg) {
        if (arg.value().inner instanceof FloatValue v) return v.value();
        else throw coerceError(arg, "float");
    }

    public boolean asBool(Arg arg) {
        if (arg.value().inner instanceof BoolValue v) return v.value();
        else throw coerceError(arg, "bool");
    }

    public String asStr(Arg arg) {
        if (arg.value().inner instanceof StrValue v) return v.value();
        else throw coerceError(arg, "str");
    }

    public ArrayList<Value> asList(Arg arg) {
        if (arg.value().inner instanceof ListValue v) return v.elems();
        else throw coerceError(arg, "list");
    }

    public Value asRef(Arg arg) {
        if (arg.value().inner instanceof ReferenceValue v) {
            return v.referent();
        } else {
            var format = "function `%s` expected reference as argument, but type %s was supplied"; 
            throw new LangError(arg.pos(), format, name(), arg.type().name());
        }
    }

    LangError coerceError(Arg arg, String type) {
        var format = "function `%s` expected argument type %s, but type %s was supplied"; 
        throw new LangError(arg.pos(), format, name(), type, arg.type().name());
    }
}
