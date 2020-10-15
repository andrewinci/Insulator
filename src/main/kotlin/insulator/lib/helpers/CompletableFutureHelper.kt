package insulator.lib.helpers

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import javafx.application.Platform
import org.apache.kafka.common.KafkaFuture
import java.util.concurrent.CompletableFuture

fun <T> KafkaFuture<T>.toCompletableFuture(): CompletableFuture<Either<Throwable, T>> {
    val wrappingFuture = CompletableFuture<Either<Throwable, T>>()
    this.whenComplete { value: T, throwable: Throwable? ->
        if (throwable != null) {
            wrappingFuture.complete(throwable.left())
        } else {
            wrappingFuture.complete(value.right())
        }
    }
    return wrappingFuture
}

fun <A, B> CompletableFuture<Either<Throwable, A>>.map(f: (A) -> B): CompletableFuture<Either<Throwable, B>> = this.thenApply { it.map(f) }

fun <A, B> CompletableFuture<Either<Throwable, A>>.flatMap(f: (A) -> Either<Throwable, B>): CompletableFuture<Either<Throwable, B>> = this.thenApply { it.flatMap(f) }

fun <A, B> CompletableFuture<Either<Throwable, A>>.fold(ifLeft: (Throwable) -> B, ifRight: (A) -> B):
    CompletableFuture<B> = this.thenApply { it.fold(ifLeft, ifRight) }

fun <T> T.runOnFXThread(f: T.() -> Unit) = Platform.runLater { this.apply(f) }

fun <A, B> CompletableFuture<Either<Throwable, A>>.completeOnFXThread(f: (A) -> B): CompletableFuture<Either<Throwable, B>> {
    val result = CompletableFuture<Either<Throwable, B>>()
    this.thenApply { result.runOnFXThread { complete(it.map { f(it) }) } }
    return result
}

fun <A> CompletableFuture<Either<Throwable, A>>.handleErrorWith(f: (Throwable) -> Unit): CompletableFuture<Unit> =
    this.thenApply { result -> result.fold({ f(it) }, { }) }
