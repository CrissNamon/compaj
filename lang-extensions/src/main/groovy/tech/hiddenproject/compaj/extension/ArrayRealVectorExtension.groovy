package tech.hiddenproject.compaj.extension

import org.apache.commons.math3.linear.ArrayRealVector
import tech.hiddenproject.compaj.lang.extension.Extension

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
