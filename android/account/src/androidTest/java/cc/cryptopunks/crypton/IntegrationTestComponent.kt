package cc.cryptopunks.crypton

import cc.cryptopunks.crypton.net.Net
import cc.cryptopunks.crypton.entity.Account
import cc.cryptopunks.crypton.interactor.*
import cc.cryptopunks.crypton.service.ReconnectAccountsService

internal interface IntegrationTestComponent {

    val accountRepo: Account.Repo
    val clientManager: Net.Manager

    val addAccount: AddAccountInteractor
    val connectAccount: ConnectAccountInteractor
    val registerAccount: RegisterAccountInteractor
    val disconnectAccount: DisconnectAccountInteractor
    val deleteAccount: DeleteAccountInteractor
    val unregisterAccount: UnregisterAccountInteractor
    val reconnectAccounts: ReconnectAccountsService

    fun clearDatabase()
}