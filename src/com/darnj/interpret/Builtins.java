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
                var message = "function `fmt` expected non-undefined argument, but type undefined was supplied";
                throw new LangError(arg.pos(), message);
            }
            return Value.makeStr(arg.value().toString());
        });

        fn("concat", c -> {
            if (c.args().isEmpty()) {
                var message = "function `concat` takes 1 or more arguments, but 0 were supplied"; 
                throw new LangError(c.pos(), message);
            }

            var str = new StringBuilder();
            for (var arg : c.args()) {
                var substr = c.asStr(arg);
                str.append(substr);
            }
            return Value.makeStr(str.toString());
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
            list.add(value.move());
            return Value.makeUndefined();
        });

        fn("pop", c -> {
            c.arity(1);
            var list = c.asList(c.arg(0));
            if (list.isEmpty()) {
                return Value.makeNil();
            }
            return list.removeLast().move();
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

        fn("len", c -> {
            c.arity(1);
            var arg = c.arg(0);
            var len = switch (arg.value().inner) {
                case ListValue a -> a.elems().size();
                case StrValue a -> a.value().length();
                default -> {
                    var format = "function `len` expected argument type str or list, but type %s was supplied";
                    throw new LangError(arg.pos(), String.format(format, arg.type()));
                }
            };
            return Value.makeInt(len);
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

        fn("assert", c -> {
            c.arity(1);
            var arg = c.arg(0);
            var cond = c.asBool(arg);
            if (!cond) {
                throw new LangError(arg.pos(), "assertion failed");
            }
            return Value.makeUndefined();
        });

        fn("throw", c -> {
            c.arity(1);
            var arg = c.arg(0);
            var msg = c.asStr(arg);
            throw new LangError(arg.pos(), msg);
        });
    }

    void fn(String name, Function impl) {
        ctx.funcs.put(ctx.symbols().intern(name), impl);
    }
}
