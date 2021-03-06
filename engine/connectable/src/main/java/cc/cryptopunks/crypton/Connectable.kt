package cc.cryptopunks.crypton

import cc.cryptopunks.crypton.util.OpenStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

interface Subscription {
    val enable: Boolean
}

interface Connectable : CoroutineScope {
    interface Binding {
        class Store : OpenStore<List<WeakReference<out Binding>>>(emptyList())

        val services: Set<Connectable>
        suspend fun cancel(cause: CancellationException? = null)
        operator fun plus(service: Connectable?): Boolean
        operator fun minus(service: Connectable?): Boolean
        fun send(any: Any) = Unit
    }

    val id: Any get() = this::class.java.simpleName

    fun Connector.connect(): Job = launch { }
    fun Connector.connect(service: Connectable): Job = launch {
        service.run { connect() }.join()
    }
}

inline fun <reified T : Connectable> Connectable.Binding.minus() =
    services.filterIsInstance<T>().forEach { minus(it) }

interface Actor : Connectable {
    interface Status
    object Start : Status
    object Stop : Status
    object Connected : Status

    companion object {
        val Empty = object : Actor {
            override val coroutineContext get() = Dispatchers.Unconfined
        }
    }
}

fun Connectable.dispatch(
    vararg args: Any,
    output: suspend (Any) -> Unit = {}
) = connector(*args, output).connect()

fun Any.connector(
    output: suspend (Any) -> Unit = {}
) = Connector(
    input = flowOf(this),
    output = output
)

fun connector(
    vararg args: Any,
    output: suspend (Any) -> Unit = {}
) = Connector(
    input = args.asFlow(),
    output = output
)

data class Connector(
    val input: Flow<Any>,
    val close: () -> Unit = {},
    val output: suspend (Any) -> Unit = {}
) {
    suspend fun Any.out() = output(this)
}

typealias ConnectorOutput = suspend (Any) -> Unit

fun Connector.actor(): Actor = object : Actor, Connectable by ConnectableConnector(this) {}

private class ConnectableConnector(
    private val connector: Connector
) : Connectable {
    override val coroutineContext = SupervisorJob() + Dispatchers.Unconfined
    override fun Connector.connect(): Job = launch {
        launch { input.collect(connector.output) }
        launch { connector.input.collect(output) }
    }
}
