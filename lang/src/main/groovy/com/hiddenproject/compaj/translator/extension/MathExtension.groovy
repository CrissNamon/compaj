package com.hiddenproject.compaj.translator.extension


import java.lang.reflect.Method
import java.util.stream.Collectors

class MathExtension implements Extension {

    @Override
    void extend(Script instance) {
        for (Method m : Math.methods) {
            if (m.getParameterCount() != 1) continue
            if (!m.getParameterTypes().any {
                it.getSuperclass() == Number.class
                        || (it.isPrimitive() && it != char.class && it != boolean.class)
            }) continue
            String method = m.getName()
            Math.metaClass.static."${method}" << fromList(method)
            Math.metaClass.static."${method}" << fromArray(method)
            instance.metaClass."${method}" << fromArray(method)
        }
    }

    private static Closure fromList(String method) {
        {
            List<Number> data ->
                data.stream()
                        .map(x -> Math."${method}"(x.doubleValue()))
                        .collect(Collectors.toList())
        }
    }

    private static Closure fromArray(String method) {
        {
            Number... data ->
                Arrays.stream(data)
                        .map(d -> Math."${method}"(d.doubleValue()))
                        .collect(Collectors.toList())
                        .toArray(new Double[]{})
        }
    }

}
