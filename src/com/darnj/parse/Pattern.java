package com.darnj.parse;

import com.darnj.op.*;

interface Pattern {
    Op parse(Parser parser);
}