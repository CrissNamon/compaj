package tech.hiddenproject.compaj.extension

import org.apache.commons.math3.complex.Complex
import tech.hiddenproject.compaj.lang.extension.Extension

import java.lang.reflect.Method
import java.util.stream.Collectors

/**
 * Extension for Math to make functions accept list of parameters.
 */
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
        addMathConstants(instance)
    }

    private void addMathConstants(Script instance) {
        instance.metaClass.'$pi' = Math.PI
        instance.metaClass.'$i' = new Complex(0, 1)
        instance.metaClass.'$e' = Math.E
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
