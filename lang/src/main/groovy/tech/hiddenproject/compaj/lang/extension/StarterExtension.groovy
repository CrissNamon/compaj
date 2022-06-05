package tech.hiddenproject.compaj.lang.extension

class StarterExtension implements Extension {
    @Override
    void extend(Script instance) {
        ExpandoMetaClass.enableGlobally()
    }
}
