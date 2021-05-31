package org.skyworkz.profile.messages.processors

import org.skyworkz.profile.messages.events.Event

interface IEventProcessor {
    fun process(event : Event)
}