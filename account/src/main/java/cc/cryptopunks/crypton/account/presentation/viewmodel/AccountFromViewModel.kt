package cc.cryptopunks.crypton.account.presentation.viewmodel

import cc.cryptopunks.crypton.account.domain.command.ConnectAccount
import cc.cryptopunks.crypton.account.domain.command.DisconnectAccount
import cc.cryptopunks.crypton.common.HandleError
import cc.cryptopunks.crypton.core.entity.Account
import cc.cryptopunks.crypton.core.entity.Account.Status.Connected
import cc.cryptopunks.crypton.core.entity.Account.Status.Connecting
import cc.cryptopunks.crypton.core.module.ViewModelScope
import cc.cryptopunks.crypton.core.util.AsyncExecutor
import cc.cryptopunks.crypton.core.util.Input
import cc.cryptopunks.crypton.core.util.ViewModel
import cc.cryptopunks.kache.core.Kache
import cc.cryptopunks.kache.core.KacheManager
import cc.cryptopunks.kache.core.lazy
import cc.cryptopunks.kache.rxjava.observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jivesoftware.smack.sasl.SASLErrorException
import org.jxmpp.stringprep.XmppStringprepException
import javax.inject.Inject

@ViewModelScope
class AccountViewModel @Inject constructor(
    private val errorPublisher: HandleError.Publisher
) : () -> Disposable,
    ViewModel,
    Kache.Provider by KacheManager() {

    val serviceName by lazy<Input>("serviceName")
    val userName by lazy<Input>("userName")
    val password by lazy<Input>("password")
    val confirmPassword by lazy<Input>("confirmPassword")
    val onClick by lazy("loginButton", initial = 0L)
    val errorMessage by lazy("errorMessage", initial = "")

    fun getAccount() = Account(
        domain = serviceName.value.text,
        credentials = Account.Credentials(
            userName = userName.value.text,
            password = password.value.text
        )
    )

    override fun invoke(): Disposable = CompositeDisposable(
        errorPublisher.observable()
            .filter { it is Account.Exception }
            .map { it.cause!! }
            .subscribe { throwable ->
                errorMessage(
                    when (throwable) {
                        is SASLErrorException -> throwable.getErrorMessage()
                        is XmppStringprepException -> throwable.localizedMessage
                        else -> throwable.localizedMessage
                    }
                )
            },
        onClick.observable().filter { it > 0 }.subscribe {
            errorMessage.value = ""
        }
    )
}

fun SASLErrorException.getErrorMessage() = saslFailure.saslError.toString().replace("_", " ")

class AccountItemViewModel @Inject constructor(
    private val async: AsyncExecutor,
    private val connectAccount: ConnectAccount,
    private val disconnectAccount: DisconnectAccount
) {
    var account: Account = Account.Empty

    val name get() = account.jid

    val status get() = account.status.name

    val isChecked get() = account.status == Connected

    val isEnabled get() = account.status != Connecting

    fun toggleConnection() = async(
        task = when (account.status) {
            Connected -> disconnectAccount
            else -> connectAccount
        }
    )(account.id)

    fun remove() = async(task = disconnectAccount)(account.id)
}