package test.fixtures.registry

import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterEvent
import io.github.dawidprosba.aergiadevkit.ksp.registry.annotations.RegisterGlobalEvent

class TestEventHandlers {
    companion object {
        @RegisterGlobalEvent(StubEvent::class)
        fun onGlobalEvent() {}

        @RegisterGlobalEvent(StubEvent::class)
        fun onGlobalEventWithParam(event: StubEvent) {}

        @RegisterGlobalEvent(StubEvent::class, enabled = false)
        fun onDisabledGlobalEvent() {}

        @RegisterGlobalEvent(StubEvent::class)
        fun <E : StubEvent> onGlobalEventWithTypeParam(event: E) {}

        @RegisterEvent(StubEvent::class, StubSubject::class)
        fun onEvent() {}

        @RegisterEvent(StubEvent::class, StubSubject::class)
        fun onEventWithParam(event: StubEvent) {}

        @RegisterEvent(StubEvent::class, StubSubject::class, enabled = false)
        fun onDisabledEvent() {}
    }
}
