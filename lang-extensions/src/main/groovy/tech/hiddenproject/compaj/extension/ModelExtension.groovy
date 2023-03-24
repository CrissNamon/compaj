package tech.hiddenproject.compaj.extension


import tech.hiddenproject.compaj.core.data.NamedFunction
import tech.hiddenproject.compaj.core.model.Model
import tech.hiddenproject.compaj.lang.extension.Extension

/**
 * Extension for Model to support Groovy features.
 */
class ModelExtension implements Extension {
    @Override
    void extend(Script instance) {
        Model.metaClass.call = {
            Map m ->
                m = m.collectEntries { k, v ->
                    type = delegate.fns().get(k).getGenericAsArray(delegate.fns().get(k).getClass(), NamedFunction.class.getName(), 1)
                    [k, v.asType(type)]
                }
                delegate.compute(m)
        }
        Model.metaClass.putAt = {
            l, NamedFunction f -> delegate.a(f)
        }
    }
}
