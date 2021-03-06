package cc.cryptopunks.crypton.context

import kotlinx.coroutines.flow.Flow

interface Net :
    Account.Net,
    Presence.Net,
    Message.Net,
    Chat.Net,
    Roster.Net {

    fun connect()
    fun disconnect()
    fun interrupt()
    fun isConnected(): Boolean
    suspend fun initOmemo()
    fun isOmemoInitialized(): Boolean
    fun netEvents(): Flow<Api.Event>

    interface Event : Api.Event

    object Connected : Event
    object OmemoInitialized : Event
    data class Disconnected(
        val throwable: Throwable? = null
    ) : Event {
        val hasError get() = throwable != null
    }

    open class Exception(
        message: String? = null,
        cause: Throwable? = null
    ) :
        kotlin.Exception(message, cause),
        Event
}
