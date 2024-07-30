package com.darnj.interpret;

import java.util.ArrayList;

import com.darnj.op.Op;
import com.darnj.type.*;

public final class Function {
    int func;
    ArrayList<IdentWithType> params;
    Type returnType;
    Op body;

    public Function(int func, ArrayList<IdentWithType> params, Type returnType, Op body) {
        this.func = func;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}
