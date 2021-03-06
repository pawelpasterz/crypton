package cc.cryptopunks.crypton.handler

import cc.cryptopunks.crypton.context.Chat
import cc.cryptopunks.crypton.context.SessionScope
import cc.cryptopunks.crypton.handle

internal fun SessionScope.handleGetMessages() =
    handle<Chat.Service.GetMessages> {
        messageRepo.list(
            range = System.currentTimeMillis().let { currentTime ->
                currentTime - SEVEN_DAYS_MILLIS..currentTime
            }
        ).let { messages ->
            when {
                address != null -> messages.filter { it.chat == address }
                else -> messages
            }
        }
    }

const val SEVEN_DAYS_MILLIS = 1000 * 60 * 60 * 24 * 7
