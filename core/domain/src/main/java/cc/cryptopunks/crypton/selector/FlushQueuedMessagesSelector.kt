package cc.cryptopunks.crypton.selector

import cc.cryptopunks.crypton.context.Chat
import cc.cryptopunks.crypton.context.Net
import cc.cryptopunks.crypton.context.Presence
import cc.cryptopunks.crypton.context.SessionScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


internal fun SessionScope.flushMessageQueueFlow(): Flow<Chat.Service.FlushQueuedMessages> =
    flowOf(
        netEvents().filterIsInstance<Net.OmemoInitialized>().map {
            messageRepo.queuedList().map { it.chat }
        },
        presenceChangedFlow().filter {
            it.presence.status == Presence.Status.Subscribed
        }.map {
            listOf(it.presence.resource.address)
        },
        messageRepo.queuedListFlow().map { list ->
            list.map { it.chat }
        }
    ).flattenMerge().filter {
        isOmemoInitialized()
    }.map {
        Chat.Service.FlushQueuedMessages(it.toSet())
    }
