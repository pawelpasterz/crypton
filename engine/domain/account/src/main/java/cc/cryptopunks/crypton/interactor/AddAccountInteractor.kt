package cc.cryptopunks.crypton.interactor

import cc.cryptopunks.crypton.context.Account
import cc.cryptopunks.crypton.context.Service
import cc.cryptopunks.crypton.manager.AccountManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AddAccountInteractor @Inject constructor(
    scope: Service.Scope,
    manager: AccountManager,
    login: LoginAccountInteractor,
    deleteAccount: DeleteAccountInteractor
) : (Account) -> Job by { account ->
    scope.launch {
        manager.copy().run(
            onAccountException = deleteAccount
        ) {
            insert(account)
            login.suspend(account.address)
        }
    }
}