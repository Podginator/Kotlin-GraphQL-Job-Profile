package org.skyworkz.profile.messages

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.util.BlockingArrayQueue
import org.skyworkz.profile.messages.events.Event
import org.skyworkz.profile.messages.processors.IEventProcessor
import org.skyworkz.profile.util.repeatUntilCancelled

class CoroutineEventBroker(private val eventProcessor : IEventProcessor) : IEventBroker {
    private val channel = Channel<Event>()

    override fun commit(event: Event) {
        GlobalScope.launch {
            channel.send(event)
        }
    }

    fun consume() = GlobalScope.launch {
        repeatUntilCancelled {
            for (event in channel) {
                eventProcessor.process(event)
            }
        }
    }

}