package cc.cryptopunks.crypton.util.ext

import cc.cryptopunks.kache.core.Kache

inline operator fun <T : Any> Kache<T>.invoke(reduce: T.() -> T?) {
    value.reduce()?.let {
        value = it
    }
}

operator fun <T: Any> Kache<List<T>>.plus(item: T) = invoke {
    value + item
}

operator fun <T: Any> Kache<List<T>>.minus(item: T) = invoke {
    value - item
}