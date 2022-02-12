package insulator.helper

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

// run catching and fold the response to Either
fun <I, R> I.runCatchingE(f: I.() -> R) = this.runCatching(f).fold({ it.right() }, { it.left() })

fun <A, B> List<Either<A, B>>.toEitherOfList() =
    this.fold(emptyList<B>().right() as Either<A, List<B>>) { lst, v ->
        lst.flatMap {
            when (v) {
                is Either.Left<A> -> v
                is Either.Right<B> -> it.plus(v.value).right()
            }
        }
    }
