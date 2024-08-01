package com.darnj.interpret;

import java.util.ArrayList;
import java.util.HashMap;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.op.*;
import com.darnj.type.*;
import com.darnj.value.*;

public final class UserFunction implements Function {
    int func;
    Span pos;
    ArrayList<Param> params;
    Type returnType;
    Op body;

    public UserFunction(int func, Span pos, ArrayList<Param> params, Type returnType, Op body) {
        this.func = func;
        this.pos = pos;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public Value eval(Callee callee) {
        var callerVars = callee.ctx().vars;
        callee.ctx().vars = new HashMap<>();

        try {
            callee.arity(params.size());
            callee.types(params);
            body.eval(callee.ctx());

            if (!returnType.eq(UndefinedType.instance())) {
                var format = "function `%s` marks return as type %s, but implicitly returns type undefined";
                throw new LangError(pos, String.format(format, callee.name(), returnType));
            }
            return Value.makeUndefined();
        } catch (ContinueEffect e) {
            throw new LangError(e.pos(), "cannot continue outside of a loop");
        } catch (BreakEffect e) {
            throw new LangError(e.pos(), "cannot break outside of a loop");
        } catch (ReturnEffect e) {
            if (!e.value().type().eq(returnType)) {
                var format = "function `%s` marks return as type %s, but actually returns type %s";
                throw new LangError(e.pos(), String.format(format, callee.name(), returnType, e.value().type()));
            }
            return e.value();
        } finally {
            callee.ctx().vars = callerVars;
        }
    }
}
