package insulator.lib.helpers

import arrow.core.Either
import arrow.core.right
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import org.testfx.api.FxToolkit
import java.util.concurrent.CompletableFuture

class CompletableFutureHelperKtTest : FunSpec({

    test("runOnFXThread") {
        // arrange
        FxToolkit.registerPrimaryStage()
        val cf = CompletableFuture<Either<Throwable, Int>>()
        cf.complete(1.right())
        // act
        val res = cf.completeOnFXThread {
            it + 1
        }
        // assert
        res.get() shouldBeRight 2
    }
})
