package cc.cryptopunks.crypton.context

import androidx.paging.DataSource
import androidx.paging.PagedList
import cc.cryptopunks.crypton.Subscription
import kotlinx.coroutines.flow.Flow

data class Chat(
    val address: Address = Address.Empty,
    val account: Address = Address.Empty,
    val users: List<Address> = emptyList(),
    val title: String = ""
) {
    val isConference = address.isConference

    companion object {
        val Empty = Chat()
    }

    data class Member(
        val nick: String?,
        val address: Address,
        val role: Role,
        val affiliation: Affiliation
    )

    enum class Role { moderator, none, participant, visitor, unknown }

    enum class Affiliation { owner, admin, member, outcast, none, unknown }

    object Service {

        // command

        object PopClipboard

        data class MessagesRead(val messages: List<Message>)

        data class EnqueueMessage(val text: String, val encrypted: Boolean = true)

        data class FlushQueuedMessages(val addresses: Set<Address> = emptySet())

        object ClearInfoMessages

        data class UpdateNotification(val account: Address, val messages: List<Message>)

        data class Copy(val message: Message)

        data class Delete(val message: Message)

        data class Create(val chat: Chat)

        data class Invite(val users: List<Address>, val chat: Address = Address.Empty)

        data class DeleteChat(val chats: List<Address>)

        data class Configure(val account: Address? = null, val chat: Address? = null)

        // Query

        data class InfoMessage(val text: String)

        object GetPagedMessages

        data class GetMessages(val address: Address? = null)

        data class ListJoinedRooms(val account: Address)

        data class ListRooms(val accounts: Set<Address> = emptySet())

        data class GetInfo(val chat: Address? = null, val account: Address? = null)

        // Subscribe

        data class SubscribePagedMessages(override val enable: Boolean) : Subscription

        data class SubscribeLastMessage(override val enable: Boolean) : Subscription

        data class SubscribeListMessages(
            override var enable: Boolean = true,
            val from: Long = 0,
            val to: Long = System.currentTimeMillis()
        ) : Subscription

        // Event

        data class ChatCreated(val chat: Address)

        data class MessageText(val text: CharSequence?)

        data class PagedMessages(val account: Address, val list: PagedList<Message>)

        data class Messages(val account: Address, val list: List<Message>)

        interface Rooms {
            val set: Set<Address>
        }

        data class JoinedRooms(override val set: Set<Address>) : Rooms

        data class AllRooms(override val set: Set<Address>) : Rooms

        data class Info(
            val name: String,
            val account: Address,
            val address: Address,
            val members: Set<Member> = emptySet()
        )
    }


    interface Net {
        fun supportEncryption(address: Address): Boolean
        fun createOrJoinConference(chat: Chat): Chat
        fun configureConference(chat: Address)
        fun inviteToConference(chat: Address, users: List<Address>)
        fun conferenceInvitationsFlow(): Flow<ConferenceInvitation>
        fun joinConference(address: Address, nickname: String, historySince: Int = 0)
        fun listJoinedRooms(): Set<Address>
        fun listRooms(): Set<Address>
        fun getChatInfo(chat: Address): Service.Info

        interface Event : Api.Event
        data class Joined(val chat: Chat) : Event

        interface EventFlow : Flow<Event>

        data class ConferenceInvitation(
            val address: Address,
            val inviter: Resource,
            val reason: String?,
            val password: String?
        )
    }


    interface Repo {
        suspend fun get(address: Address): Chat
        suspend fun contains(address: Address): Boolean
        suspend fun list(): List<Chat>
        suspend fun list(addresses: List<Address>): List<Chat>
        suspend fun insert(chat: Chat)
        suspend fun insertIfNeeded(chat: Chat)
        suspend fun delete(chats: List<Address>)
        suspend fun deleteAll()
        fun dataSourceFactory(): DataSource.Factory<Int, Chat>
        fun flowList(): Flow<List<Chat>>
    }
}

suspend fun SessionScope.createChat(chat: Chat) {
    log.d("Creating $chat")
    if (chat.isConference && chat.address !in listJoinedRooms()) createOrJoinConference(chat)
    log.d("Chat ${chat.address} with users ${chat.users} created")
    insertChat(chat)
}

suspend fun SessionScope.insertChat(chat: Chat) {
    log.d("Inserting $chat")
    chatRepo.insertIfNeeded(chat)
    log.d("Chat ${chat.address} with users ${chat.users} Inserted")
}
