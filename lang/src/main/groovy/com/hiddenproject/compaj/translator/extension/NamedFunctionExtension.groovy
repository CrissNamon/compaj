package com.hiddenproject.compaj.translator.extension

import com.hiddenproject.compaj.core.data.NamedFunction
import com.hiddenproject.compaj.core.model.DynamicFunction

import java.lang.reflect.Array

class NamedFunctionExtension implements Extension {
    @Override
    void extend(Script instance) {
        NamedFunction.metaClass.call = {
            Object... d ->
                if (d.length == 0) return delegate.value()
                Class type = NamedFunction.getGeneric(delegate.getClass(), NamedFunction.class.getName(), 1)
                Class Otype = NamedFunction.getGeneric(delegate.getClass(), NamedFunction.class.getName(), 2)
                Object[] arr = (NamedFunction.cast(type, Array.newInstance(type, d.length)))
                for (int i = 0; i < d.length; i++) arr[i] = d[i].asType(type)
                delegate.value(arr)
        }

        NamedFunction.metaClass.b = {
            Closure s ->
                Class Otype = NamedFunction.getGeneric(delegate.getClass(), NamedFunction.class.getName(), 2)
                delegate.b(DynamicFunction.fromClosureWithCast(s, Otype, { r -> r.asType(Otype) }))
        }

        NamedFunction.metaClass.constructor = {
            name, Closure f ->
                Class Otype = NamedFunction.getGeneric(delegate, NamedFunction.class.getName(), 2)
                delegate
                        .getDeclaredConstructor(name.getClass(), DynamicFunction.class)
                        .newInstance(name, DynamicFunction.fromClosureWithCast(f, Otype, { r -> r.asType(Otype) }))
        }

        NamedFunction.metaClass.call = {
            ArrayList d ->
                Class type = delegate.getGenericAsArray(delegate.getClass(), NamedFunction.class.getName(), 1)
                Object[] arr = d.asType(type)
                delegate.value(arr)
        }
    }
}
