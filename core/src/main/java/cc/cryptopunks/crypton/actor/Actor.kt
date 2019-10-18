package cc.cryptopunks.crypton.actor

import cc.cryptopunks.crypton.presenter.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

interface Actor {
    class Scope : Actor, CoroutineScope by MainScope() {
        operator fun <A : Actor, P : Presenter<A>> invoke(actor: A?, presenter: P?) =
            actor?.also { view -> presenter?.run { launch { view() } } }
    }
}