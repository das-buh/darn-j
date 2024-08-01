package com.darnj.interpret;

import com.darnj.value.*;

public interface Function {
    Value eval(Callee ctx);
}
