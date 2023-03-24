package tech.hiddenproject.compaj.extension

import tech.hiddenproject.compaj.lang.extension.Extension

/**
 * Extension to enable ExpandoMetaClass.
 */
class StarterExtension implements Extension {
    @Override
    void extend(Script instance) {
        ExpandoMetaClass.enableGlobally()
    }
}
