package cc.cryptopunks.crypton.translator

data class Command(
    val params: List<Input>,
    val args: List<String>,
    val input: List<String>,
    val run: Context.(List<String>) -> Any
)

fun Command.canExecute() =
    params.filterNot { it is Input.Vararg }.all(Input::isNotEmpty)

fun Command.emptyParams() =
    params.filter(Input::isEmpty)

fun command(
    vararg params: Input,
    run: Context.(List<String>) -> Any
) = Command(
    params = params.toList(),
    args = emptyList(),
    run = run,
    input = emptyList()
)


fun Command.append(arg: String) = copy(
    input = input + arg,
    args = params.fold(args + arg) { args, param ->
        when {

            args.isEmpty() -> args

            param is Input.Vararg -> param.run {
                value = listOfNotNull(value)
                    .plus(args)
                    .joinToString(" ")
                emptyList<String>()
            }

            param.value != null -> args

            param is Input.Param -> {
                param.value = args.first()
                args.drop(1)
            }

            param is Input.Named -> {
                args.indexOf(param.name).plus(1).let { index ->
                    if (index !in 1 until args.size) args
                    else {
                        param.value = args[index]
                        args.filterIndexed { i, _ -> i != index - 1 && i != index }
                    }
                }
            }

            else -> throw Exception("Unknown param")
        }
    }
)
