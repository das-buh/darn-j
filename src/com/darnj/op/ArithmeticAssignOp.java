package com.darnj.op;

import com.darnj.op.arithmetic.*;
import com.darnj.value.*;

public abstract class ArithmeticAssignOp extends AssignmentOp {
    abstract Arithmetic arithmetic();

    @Override
    void assign(Value assignee, Value value) {
        var arithmetic = arithmetic();
        Value result;
        
        if (assignee.inner instanceof IntValue a && value.inner instanceof IntValue v) {
            result = arithmetic.evalInt(a.value(), v.value());
        } else if (assignee.inner instanceof FloatValue a && value.inner instanceof FloatValue v) {
            result = arithmetic.evalFloat(a.value(), v.value());
        } else {
            throw error(
                "cannot %s types %s and %s", 
                arithmetic.opName(),
                assignee.type().name(), 
                value.type().name()
            );
        }

        assignee.inner = result.inner;
    }
}
