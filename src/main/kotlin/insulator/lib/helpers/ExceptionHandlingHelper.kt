package insulator.lib.helpers

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

fun <R> Result<R>.toEither() = this.fold({ it.right() }, { it.left() })
fun <R, T : Throwable> Result<R>.toEither(f: (Throwable) -> T) = this.fold({ it.right() }, { it.left() }).mapLeft { f(it) }

fun <I, R> I.runCatching_(f: I.() -> R) = this.runCatching(f).toEither()

fun <A, B> List<Either<A, B>>.toEitherOfList() =
    this.fold(emptyList<B>().right() as Either<A, List<B>>) { lst, v ->
        lst.flatMap {
            when (v) {
                is Either.Left<A> -> v
                is Either.Right<B> -> it.plus(v.b).right()
            }
        }
    }
