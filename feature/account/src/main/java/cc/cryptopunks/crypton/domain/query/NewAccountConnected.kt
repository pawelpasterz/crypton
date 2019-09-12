package cc.cryptopunks.crypton.domain.query

import cc.cryptopunks.crypton.entity.Account
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.max

class NewAccountConnected @Inject constructor(
    private val dao: Account.Dao
) : () -> Flow<Long> {

    override fun invoke(): Flow<Long> = dao
        .flowList()
        .drop(1)
        .map { it.getConnectedIds() }
        .filter { it.isNotEmpty() }
        .map { it.last() }
        .scanReduce { l, r ->  max(l, r) }
        .distinctUntilChanged()
}

private fun List<Account>.getConnectedIds() = this
    .filter { it.status == Account.Status.Connected }
    .map(Account::id)