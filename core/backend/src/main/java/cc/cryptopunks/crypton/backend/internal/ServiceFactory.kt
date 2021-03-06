package cc.cryptopunks.crypton.backend.internal

import cc.cryptopunks.crypton.context.AppScope
import cc.cryptopunks.crypton.Connectable
import cc.cryptopunks.crypton.context.Route
import cc.cryptopunks.crypton.context.Route.Chat
import cc.cryptopunks.crypton.createHandlers
import cc.cryptopunks.crypton.service.accountHandlers
import cc.cryptopunks.crypton.service.appChatHandlers
import cc.cryptopunks.crypton.service.rosterHandlers
import cc.cryptopunks.crypton.HandlerRegistryFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

internal class ServiceFactory(
    private val appScope: AppScope
) : (Route<*>) -> Connectable? {
    override fun invoke(route: Route<*>): Connectable? = when (route) {

        Route.Main -> appScope

        is Chat -> runBlocking {
            delay(100)
            appScope.sessionScope(route.account).chatScope(route.address)
        }

        else -> null
    }
}


val mainHandlers: HandlerRegistryFactory<AppScope> = {
    createHandlers {
        +accountHandlers()
        +rosterHandlers()
        +appChatHandlers()
    }
}
