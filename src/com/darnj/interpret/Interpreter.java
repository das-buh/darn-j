package com.darnj.interpret;

import com.darnj.LangError;
import com.darnj.op.*;
import com.darnj.parse.*;

public final class Interpreter {
    String src;
    Context ctx;
    
    Interpreter(String src) {
        this.src = src;
        ctx = new Context();
        Builtins.addBuiltins(ctx);
    }

    public static void interpret(String src) {
        try {
            new Interpreter(src).parseAndEval();
        } catch (LangError e) {
            e.render(src);
        }
    }

    void parseAndEval() {
        try {
            Parser.parse(src, ctx).eval(ctx);
        } catch (ContinueEffect e) {
            throw new LangError(e.pos(), "cannot continue outside of a loop");
        } catch (BreakEffect e) {
            throw new LangError(e.pos(), "cannot break outside of a loop");
        } catch (ReturnEffect e) {
            throw new LangError(e.pos(), "cannot return outside of a function");
        }
    }
}
