package insulator.lib.helpers

import arrow.core.Either
import arrow.core.right
import helper.FxContext
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import java.util.concurrent.CompletableFuture

class CompletableFutureHelperKtTest : StringSpec({

    FxContext().use {
        "test complete on FX thread happy path" {

            // arrange
            val cf = CompletableFuture<Either<Throwable, Int>>()
            cf.complete(1.right())
            // act
            val res = cf.completeOnFXThread {
                it + 1
            }
            // assert
            res.get() shouldBeRight 2
        }
    }
})
