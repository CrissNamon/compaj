package tech.hiddenproject.compaj.extension

import tech.hiddenproject.compaj.lang.extension.Extension

class StarterExtension implements Extension {
    @Override
    void extend(Script instance) {
        ExpandoMetaClass.enableGlobally()
    }
}
