package cc.cryptopunks.crypton.account.presentation.viewmodel

import cc.cryptopunks.crypton.account.domain.query.ObserveAccountList
import cc.cryptopunks.crypton.common.Schedulers
import javax.inject.Inject

class AccountListViewModel @Inject constructor(
    private val observeAccounts: ObserveAccountList,
    private val schedulers: Schedulers
) {
    val observable
        get() = observeAccounts()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)!!
}