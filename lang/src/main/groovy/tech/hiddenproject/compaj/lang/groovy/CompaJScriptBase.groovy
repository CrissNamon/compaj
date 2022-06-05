package tech.hiddenproject.compaj.lang.groovy

import org.apache.commons.math3.complex.ComplexFormat
import tech.hiddenproject.compaj.lang.extension.Extension

import java.text.NumberFormat

abstract class CompaJScriptBase extends Script {

    private static Set<Extension> extensions

    static ComplexFormat COMPLEX_FORMAT

    static {
        NumberFormat _nf = NumberFormat.getInstance(Locale.US)
        COMPLEX_FORMAT = new ComplexFormat(_nf, _nf)
        extensions = new HashSet<>()
    }

    CompaJScriptBase() {
        extensions.forEach({
            try {
                it.extend(this)
            } catch (Exception e) {
                e.printStackTrace()
                throw new GroovyRuntimeException("Unable to load extension: " + it.getName())
            }
        })
    }

    static void addExtension(Extension e) {
        extensions.add(e)
    }

    void help() {
        println "Help is not available now..."
    }

}
