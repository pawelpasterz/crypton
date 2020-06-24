package cc.cryptopunks.crypton

import cc.cryptopunks.crypton.util.typedLog
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress

suspend fun Any.connectClient(
    host: String = "127.0.0.1",
    port: Int = 2323,
    block: suspend ClientDsl.() -> Unit
) {
    val log = typedLog()
    withContext(newSingleThreadContext(toString())) {
        aSocket(ActorSelectorManager(newSingleThreadContext(toString()))).tcp()
            .connect(InetSocketAddress(host, port))
            .connector(log)
            .also { connector ->
                launch { ClientDsl(connector, log).block() }
            }
    }
}
