package com.hiddenproject.compaj.translator.groovy

import com.hiddenproject.compaj.translator.extension.Extension

abstract class CompaJScriptBase extends Script {

    private static Set<Extension> extensions

    @Delegate Math math

    static {
        extensions = new HashSet<>()
    }

    CompaJScriptBase() {
        extensions.forEach({
            try{
                it.extend(this)
            }catch(Exception e) {
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
