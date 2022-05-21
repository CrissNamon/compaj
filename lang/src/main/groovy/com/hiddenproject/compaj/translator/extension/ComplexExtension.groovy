package com.hiddenproject.compaj.translator.extension

import org.apache.commons.math3.complex.Complex

class ComplexExtension implements Extension {
    @Override
    void extend(Script thisBase) {
        Complex.metaClass.plus = { Number m -> delegate.add(new Complex(m.doubleValue(), 0)) }
        Complex.metaClass.plus = { Complex c -> delegate.add(c) }
        Number.metaClass.plus << { Complex c -> c.add(delegate.doubleValue()) }
        Complex.metaClass.minus = { Number m -> delegate.subtract(new Complex(m.doubleValue(), 0)) }
        Complex.metaClass.minus = { Complex c -> delegate.subtract(c) }
        Number.metaClass.minus << { Complex c -> c.subtract(delegate.doubleValue()) }
        Complex.metaClass.multiply = { Number m -> delegate.multiply(new Complex(m.doubleValue(), 0)) }
        Number.metaClass.multiply << { Complex c -> c.multiply(delegate.doubleValue()) }
        Complex.metaClass.div = { Number m -> delegate.divide(new Complex(m.doubleValue(), 0)) }
        Complex.metaClass.div = { Complex c -> delegate.divide(c) }
        Number.metaClass.div << { Complex c -> c.divide(delegate.doubleValue()) }
    }
}
