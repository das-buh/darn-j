package com.darnj.op;

import com.darnj.value.*;

public final class Assign extends AssignmentOp {
    @Override
    void assign(Value assignee, Value value) {
        assignee.inner = value.inner;
    }
}
