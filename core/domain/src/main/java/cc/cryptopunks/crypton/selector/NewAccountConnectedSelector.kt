package cc.cryptopunks.crypton.selector

import cc.cryptopunks.crypton.context.Account
import cc.cryptopunks.crypton.context.Address
import cc.cryptopunks.crypton.context.AppScope
import cc.cryptopunks.crypton.context.SessionScope
import kotlinx.coroutines.flow.*

fun AppScope.newAccountConnectedFlow(): Flow<Address> {
    val cache = mutableSetOf<SessionScope>()

    return sessions.changesFlow()
        .flatMapConcat { map -> map.values.asFlow() }
        .filterNot { session -> session in cache }
        .flatMapMerge { session ->
            session.netEvents()
                .produceIn(session)
                .consumeAsFlow()
                .onCompletion { cache.remove(session) }
                .filterIsInstance<Account.Authenticated>()
                .mapNotNull {
                    if (session in cache) null
                    else session.also { cache.add(it) }.address
                }
        }
}
