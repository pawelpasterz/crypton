package cc.cryptopunks.crypton.handler

import cc.cryptopunks.crypton.context.Api
import cc.cryptopunks.crypton.context.Net
import cc.cryptopunks.crypton.context.SessionScope
import cc.cryptopunks.crypton.handle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun SessionScope.handleApiEvent() =
    handle<Api.Event> {
        when (this) {
            is Net.Disconnected -> if (hasError) launch {
                delay(2000) // wait for network connection
                if (!isConnected())
                    if (networkSys.status.isConnected)
                        reconnect() else
                        interrupt()
            }
        }
    }

private suspend fun SessionScope.reconnect() {
    connect()
    if (!isAuthenticated()) login()
    initOmemo()
}
