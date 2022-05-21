package com.hiddenproject.compaj.translator.extension

class StarterExtension implements Extension {
    @Override
    void extend(Script instance) {
        ExpandoMetaClass.enableGlobally()
    }
}
