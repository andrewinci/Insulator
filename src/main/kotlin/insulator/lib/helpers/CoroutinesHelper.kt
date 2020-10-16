package insulator.lib.helpers

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.kafka.common.KafkaFuture
import kotlin.coroutines.resume

fun <T : Any> T.dispatch(block: suspend T.() -> Unit) = GlobalScope.launch(Dispatchers.JavaFx) { block() }

suspend fun <T> KafkaFuture<T>.toSuspendCoroutine() = suspendCancellableCoroutine<Either<Throwable, T>> { continuation ->
    this.whenComplete { value: T, throwable: Throwable? ->
        if (throwable != null) {
            continuation.resume(throwable.left())
        } else {
            continuation.resume(value.right())
        }
    }
    continuation.invokeOnCancellation { this.cancel(true) }
}
