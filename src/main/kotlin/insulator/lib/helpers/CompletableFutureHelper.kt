package insulator.lib.helpers

import arrow.core.Either
import arrow.core.Option
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

fun <A> CompletableFuture<Either<Throwable, A>>.runOnFXThread(f: (A) -> Unit): CompletableFuture<Option<Throwable>> {
    val result = CompletableFuture<Option<Throwable>>()
    this.thenApply {
        Platform.runLater {
            it.runCatching { map(f) }
                .fold({ result.complete(Option.empty()) }, { result.complete(Option.just(it)) })
        }
    }
    return result
}

fun CompletableFuture<Option<Throwable>>.handleErrorWith(f: (Throwable) -> Unit): CompletableFuture<Unit> =
    this.thenApply { it.fold({ Unit }, f) }