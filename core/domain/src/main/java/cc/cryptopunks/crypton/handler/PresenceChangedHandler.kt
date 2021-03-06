package cc.cryptopunks.crypton.handler

import cc.cryptopunks.crypton.context.Chat
import cc.cryptopunks.crypton.context.Presence
import cc.cryptopunks.crypton.context.Resource
import cc.cryptopunks.crypton.context.Roster
import cc.cryptopunks.crypton.context.SessionScope
import cc.cryptopunks.crypton.context.createChat
import cc.cryptopunks.crypton.handle
import cc.cryptopunks.crypton.interactor.storePresence

internal fun SessionScope.handlePresenceChanged() =
    handle<Roster.Net.PresenceChanged> {
        log.d("handle $this")
        storePresence(presence)

        when (presence.status) {

            Presence.Status.Subscribe ->
                if (iAmSubscribed(presence.resource.address)) {
                    log.d("Auto accepting feedback subscription from ${presence.resource}")
                    sendPresence(
                        Presence(
                            resource = presence.resource,
                            status = Presence.Status.Subscribed
                        )
                    )
                } else {
                    log.d("Received subscription request from ${presence.resource}")
                    createChat(
                        Chat(
                            address = presence.resource.address,
                            account = address
                        )
                    )
                }
            Presence.Status.Unavailable,
            Presence.Status.Available -> if (presence.resource != Resource.Empty)
                if (presence.resource.address != address)
                    if (chatRepo.contains(presence.resource.address).not()) {
                        log.d("Creating chat from presence ${presence.resource}")
                        createChat(
                            Chat(
                                address = presence.resource.address,
                                account = address
                            )
                        )
                    }

            else -> Unit
        }
    }
