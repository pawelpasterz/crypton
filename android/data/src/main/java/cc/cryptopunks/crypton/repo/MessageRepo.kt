package cc.cryptopunks.crypton.repo

import androidx.paging.DataSource
import cc.cryptopunks.crypton.entity.*
import cc.cryptopunks.crypton.entity.MessageData
import cc.cryptopunks.crypton.entity.message
import cc.cryptopunks.crypton.entity.messageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class MessageRepo(
    private val dao: MessageData.Dao
) : Message.Repo {

    override suspend fun insert(messages: List<Message>) =
        dao.insert(messages.map { it.messageData() })

    override suspend fun insertOrUpdate(message: Message) =
        dao.insertOrUpdate(message.messageData())

    override suspend fun latest(): Message? =
        dao.latest()?.message()

    override fun flowLatest(chat: Chat): Flow<Message> =
        dao.flowLatest(chat.address.id).filterNotNull().map { it.message() }

    override fun dataSourceFactory(chat: Chat): DataSource.Factory<Int, Message> =
        dao.dataSourceFactory(chat.address.id).map { it?.message() }
}