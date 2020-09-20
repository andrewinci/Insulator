package insulator.lib.helpers

import arrow.core.Either
import arrow.core.right
import helper.cleanupFXFramework
import helper.configureFXFramework
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import java.util.concurrent.CompletableFuture

class CompletableFutureHelperKtTest : FunSpec({

    test("runOnFXThread") {
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

    beforeSpec {
        configureFXFramework()
    }
    afterSpec {
        cleanupFXFramework()
    }
})
