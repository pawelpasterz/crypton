package cc.cryptopunks.crypton.entity

import androidx.paging.DataSource
import androidx.room.*

@Entity(
    tableName = "chat",
    indices = [Index("address")],
    foreignKeys = [ForeignKey(
        entity = AccountData::class,
        parentColumns = ["id"],
        childColumns = ["address"],
        onDelete = ForeignKey.CASCADE
    )]
)
internal data class ChatData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val address: AddressData,
    val title: String
) {

    @androidx.room.Dao
    interface Dao {

        @Query("select * from chat where id = :id")
        fun get(id: Long): ChatData

        @Query("select * from chat")
        fun dataSourceFactory(): DataSource.Factory<Int, ChatData>

        @Insert
        fun insert(data: ChatData): Long

        @Insert
        fun insert(chatList: List<ChatData>): List<Long>

        @Delete
        fun delete(data: ChatData)

        @Query("delete from chat")
        fun deleteAll()
    }
}

internal fun ChatData.toDomain(users: List<User> = emptyList()) = Chat(
    id = id,
    title = title,
    address = Address.from(address),
    users = users
)

internal fun Chat.chatData() = ChatData(
    id = id,
    title = title,
    address = address.id
)