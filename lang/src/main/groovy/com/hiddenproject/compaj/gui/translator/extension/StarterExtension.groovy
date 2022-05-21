package com.hiddenproject.compaj.gui.translator.extension

class StarterExtension implements Extension {
    @Override
    void extend(Script instance) {
        ExpandoMetaClass.enableGlobally()
    }
}
