package cc.cryptopunks.crypton.domain.interactor

import cc.cryptopunks.crypton.domain.repository.AccountRepository
import cc.cryptopunks.crypton.util.wrap
import cc.cryptopunks.crypton.entity.Account
import cc.cryptopunks.crypton.entity.Account.Status.Disconnected
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AddAccountInteractor @Inject constructor(
    repository: AccountRepository,
    connect: ConnectAccountInteractor
) : (Account) -> Completable by { account ->
    repository.copy().run {
        Single.fromCallable {
            set(account)
            setStatus(Disconnected)
            insert()
        }.flatMapCompletable {
            connect(id)
        }.onErrorResumeNext {
            Completable.error(wrap(it))
        }
    }
}