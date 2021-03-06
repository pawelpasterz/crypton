package cc.cryptopunks.crypton.service

import cc.cryptopunks.crypton.collect
import cc.cryptopunks.crypton.context.SessionScope
import cc.cryptopunks.crypton.handler.handleApiEvent
import cc.cryptopunks.crypton.handler.handleConferenceInvitations
import cc.cryptopunks.crypton.handler.handleFlushMessageQueue
import cc.cryptopunks.crypton.handler.handleJoin
import cc.cryptopunks.crypton.handler.handlePresenceChanged
import cc.cryptopunks.crypton.handler.handleSaveMessages
import cc.cryptopunks.crypton.handler.handleUpdateChatNotification
import cc.cryptopunks.crypton.selector.accountAuthenticatedFlow
import cc.cryptopunks.crypton.selector.flushMessageQueueFlow
import cc.cryptopunks.crypton.selector.joinConferencesFlow
import cc.cryptopunks.crypton.selector.presenceChangedFlow
import cc.cryptopunks.crypton.selector.saveMessagesFlow
import cc.cryptopunks.crypton.selector.updateChatNotificationFlow
import handleAccountAuthenticated
import kotlinx.coroutines.launch

internal fun SessionScope.startSessionService() = launch {
    log.d("Invoke session services for $address")
    launch { netEvents().collect(handleApiEvent()) }
    launch { saveMessagesFlow().collect(handleSaveMessages(), join = true) }
    launch { presenceChangedFlow().collect(handlePresenceChanged(), join = true) }
    launch { flushMessageQueueFlow().collect(handleFlushMessageQueue(), join = true) }
    launch { conferenceInvitationsFlow().collect(handleConferenceInvitations()) }
    launch { updateChatNotificationFlow().collect(handleUpdateChatNotification()) }
    launch { accountAuthenticatedFlow().collect(handleAccountAuthenticated()) }
    launch { joinConferencesFlow().collect(handleJoin()) }
}
