package tech.hiddenproject.compaj.lang.extension

import tech.hiddenproject.compaj.core.data.Agent

class AgentExtension implements Extension {
    @Override
    void extend(Script thisBase) {
        Agent.metaClass.nearIf = { Closure c -> (a, b) -> c.call(a, b) }
        Agent.metaClass.connectsIf = { Closure c -> (a, b) -> c.call(a, b) }
        Agent.metaClass.interactsAs = { Closure c -> (a, b) -> c.call(a, b) }
        Agent.metaClass.moveAs = { Closure c -> (a, b) -> c.call(a, b) }
        Agent.metaClass.stepAs = { Closure c -> (a, b) -> c.call(a, b) }
    }
}
