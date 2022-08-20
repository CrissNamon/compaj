package tech.hiddenproject.compaj.extension

import org.apache.commons.math3.complex.Complex
import tech.hiddenproject.compaj.lang.extension.Extension

class ComplexExtension implements Extension {
    @Override
    void extend(Script thisBase) {
        Complex.metaClass.asType = { CompaJComplex t -> new CompaJComplex(delegate.getReal(), delegate.getImaginary()) }
        CompaJComplex.metaClass.plus = { Number m -> delegate.add(new CompaJComplex(m.doubleValue(), 0)) }
        CompaJComplex.metaClass.plus = { CompaJComplex c -> delegate.add(c) }
        Number.metaClass.plus << { CompaJComplex c -> c.add(delegate.doubleValue()) }
        CompaJComplex.metaClass.minus = { Number m -> delegate.subtract(new CompaJComplex(m.doubleValue(), 0)) }
        CompaJComplex.metaClass.minus = { CompaJComplex c -> delegate.subtract(c) }
        Number.metaClass.minus << { CompaJComplex c -> new CompaJComplex(delegate.doubleValue(), 0).subtract(c) }
        CompaJComplex.metaClass.multiply = { Number m -> delegate.multiply(new CompaJComplex(m.doubleValue(), 0)) }
        Number.metaClass.multiply << { CompaJComplex c -> c.multiply(delegate.doubleValue()) }
        CompaJComplex.metaClass.div = { Number m -> delegate.divide(new CompaJComplex(m.doubleValue(), 0)) }
        CompaJComplex.metaClass.div = { CompaJComplex c -> delegate.divide(c) }
        Number.metaClass.div << { CompaJComplex c -> c.divide(delegate.doubleValue()) }
    }
}
