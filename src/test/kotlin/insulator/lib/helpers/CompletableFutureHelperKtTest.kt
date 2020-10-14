package insulator.lib.helpers

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import helper.FxContext
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture

class CompletableFutureHelperKtTest : StringSpec({

    "test complete on FX thread happy path" {
        FxContext().use {
            // arrange
            val cf = CompletableFuture<Either<Throwable, Int>>()
            cf.complete(1.right())
            // act
            val res = cf.completeOnFXThread {
                it + 1
            }
            it.waitFXThread()
            // assert
            res.getNow(Throwable().left()) shouldBeRight 2
        }
    }

    "test fold completable future right" {
        FxContext().use {
            // arrange
            val cf = CompletableFuture<Either<Throwable, Int>>()
            cf.complete(0.right())
            // act
            val res = cf.fold({ 1 }, { 2 })

            // assert
            res.getNow(-1) shouldBe 2
        }
    }

    "test fold completable future left" {
        FxContext().use {
            // arrange
            val cf = CompletableFuture<Either<Throwable, Int>>()
            cf.complete(Throwable().left())
            // act
            val res = cf.fold({ 1 }, { 2 })

            // assert
            res.getNow(-1) shouldBe 1
        }
    }
})
