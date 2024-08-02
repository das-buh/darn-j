package com.darnj.op.arithmetic;

import com.darnj.value.*;

public interface Arithmetic {
    Value evalInt(long lhs, long rhs);
    Value evalFloat(double lhs, double rhs);
    String opName();
}
