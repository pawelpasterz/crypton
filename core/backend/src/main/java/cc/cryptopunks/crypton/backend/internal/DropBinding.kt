package cc.cryptopunks.crypton.backend.internal

import cc.cryptopunks.crypton.util.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

internal fun dropBindingInteractor(
    scope: CoroutineScope,
    stack: Store<List<Context>> = Store(emptyList())
): suspend () -> Unit = {
    stack.get().lastOrNull()?.also {
        scope.launch { stack reduce { dropLast(1) } }
    }?.binding?.cancel(CancellationException("Drop binding")) ?: Unit
}
