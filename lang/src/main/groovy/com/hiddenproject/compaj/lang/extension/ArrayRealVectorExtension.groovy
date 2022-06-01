package com.hiddenproject.compaj.lang.extension

import org.apache.commons.math3.linear.ArrayRealVector

class ArrayRealVectorExtension implements Extension {
    @Override
    void extend(Script instance) {
        ArrayRealVector.metaClass.constructor << {
            ArrayList<Number> d ->
                a = new double[d.size()]
                for (int i = 0; i < d.size(); i++) a[i] = d.get(i).doubleValue()
                new ArrayRealVector(a)
        }
    }
}
