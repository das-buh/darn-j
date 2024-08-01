package com.darnj.interpret;

import com.darnj.LangError;
import com.darnj.value.*;

public final class Builtins {
    Context ctx;

    Builtins(Context ctx) {
        this.ctx = ctx;
    }

    public static void addBuiltins(Context ctx) {
        new Builtins(ctx).put();
    }

    void put() {
        fn("print", c -> {
            c.arity(1);
            var str = c.asStr(c.arg(0));
            System.out.println(str);
            return Value.makeUndefined();
        });

        fn("fmt", c -> {
            c.arity(1);
            var arg = c.arg(0);
            if (arg.value().inner instanceof UndefinedValue) {
                var format = "function `%s` expected non-undefined argument, but type undefined was supplied";
                throw new LangError(arg.pos(), String.format(format, c.name()));
            }
            return Value.makeStr(arg.value().toString());
        });

        fn("concat", c -> {
            c.arity(2);
            var left = c.asStr(c.arg(0));
            var right = c.asStr(c.arg(1));
            return Value.makeStr(left + right);
        });

        fn("substr", c -> {
            c.arity(3);
            var str = c.asStr(c.arg(0));
            var left = c.asInt(c.arg(1));
            var right = c.asInt(c.arg(2));
            if (left > right || right > str.length()) {
                return Value.makeNil();
            }
            return Value.makeStr(str.substring((int) left, (int) right));
        });

        fn("push", c -> {
            c.arity(2);
            var list = c.asList(c.arg(0));
            var value = c.arg(1).value();
            list.add(value);
            return Value.makeUndefined();
        });

        fn("pop", c -> {
            c.arity(1);
            var list = c.asList(c.arg(0));
            if (list.isEmpty()) {
                return Value.makeNil();
            }
            return list.removeLast();
        });

        fn("idx", c -> {
            c.arity(2);
            var list = c.asList(c.arg(0));
            var idx = c.asInt(c.arg(1));
            if (idx >= list.size()) {
                return Value.makeNil();
            }
            return Value.makeReference(list.get((int) idx));
        });

        fn("swap", c -> {
            c.arity(2);
            var a = c.asRef(c.arg(0));
            var b = c.asRef(c.arg(1));
            var temp = a.inner;
            a.inner = b.inner;
            b.inner = temp;
            return Value.makeUndefined();
        });

        fn("put", c -> {
            c.arity(2);
            var dest = c.asRef(c.arg(0));
            var src = c.arg(1).value();
            var old = dest.inner;
            dest.inner = src.inner;
            return new Value(old);
        });
    }

    void fn(String name, Function impl) {
        ctx.funcs.put(ctx.symbols().intern(name), impl);
    }
}
