package com.darnj.value;

import java.util.ArrayList;

import com.darnj.type.*;

public final class ListValue extends ValueInternal {
    ArrayList<Value> elems;

    ListValue(ArrayList<Value> elems) {
        super(ListType.instance());
        this.elems = elems;
    }

    public ArrayList<Value> value() {
        return elems;
    }

    @Override
    boolean eq(ValueInternal other) {
        if (other instanceof ListValue o && elems.size() == o.elems.size()) {
            var len = elems.size();
            for (var i = 0; i < len; i++) {
                if (!elems.get(i).eq(o.elems.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
