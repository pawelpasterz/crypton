package cc.cryptopunks.crypton.translator

typealias TranslationContext = Context

typealias Commands = Map<Any, Map<String, Any>>

fun commands(vararg commands: Pair<Any, Map<String, Any>>): Commands = mapOf(*commands)

data class Context(
    val commands: Commands = emptyMap(),
    val route: Any = Unit,
    val isRoute: Any.() -> Boolean = { this is Unit },
    val empty: Any.() -> Any = { this },
    val account: String = "",
    val state: Any = Unit,
    val result: Any? = null
)

fun Context.process(input: String): Context = set(input).run {
    copy(result = execute()).run {
        if (result?.isRoute() == true) copy(route = result)
        else this
    }
}

fun Context.execute(): Any = anySuggestion()
    ?: (state as? Command)?.run {
        run(params.mapNotNull { it.value }).also {
            params.forEach { it.value = null }
        }
    }
    ?: IllegalStateException("Cannot execute state $state")


fun Context.prepare() = copy(
    state = commands[route.empty()]
        ?: throw IllegalArgumentException("No commands for route $route")
)

fun Context.set(string: String): Context =
    if (string.isBlank()) this
    else string.split(" ").let { strings ->
        if (strings.size != 1) set(strings)
        else when (state) {

            is Command -> state.append(string)

            is Map<*, *> -> (state as Map<String, Any>)[string]
                ?: IllegalArgumentException("Unknown command $string")

            else -> IllegalStateException("Unknown state $state")
        }.let { result ->
            when (result) {
                is Throwable -> copy(result = result)
                else -> copy(state = result)
            }
        }
    }


fun Context.set(strings: List<String>) =
    strings.fold(this) { context, string ->
        context.set(string)
    }

fun Context.anySuggestion(): Any? =
    state.let { state ->
        when (state) {

            commands[route.empty()] == state -> Check.Prepared

            is Command -> when {

                state.canExecute() -> null

                else -> Check.Suggest(
                    state.emptyParams()
                        .filterIsInstance<Input.Named>()
                        .mapNotNull(Input.Named::name)
                )
            }

            is Map<*, *> -> Check.Suggest(
                state.keys.filterIsInstance<String>()
            )

            else -> null
        }
    }

class Check {
    object Prepared
    data class Suggest(val list: List<String>)
}
