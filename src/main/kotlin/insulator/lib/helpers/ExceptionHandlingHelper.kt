package insulator.lib.helpers

import arrow.core.left
import arrow.core.right

fun <R> Result<R>.toEither() = this.fold({ it.right() }, { it.left() })
