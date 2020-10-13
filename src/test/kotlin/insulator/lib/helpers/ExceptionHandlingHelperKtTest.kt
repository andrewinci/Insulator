package insulator.lib.helpers

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class ExceptionHandlingHelperKtTest : StringSpec({

    "list of either to either of list return the list of right if all elements are right" {
        // arrange
        val sample = listOf(1.right(), 2.right(), 3.right())
        // act
        val res = sample.toEitherOfList()
        // assert
        res shouldBeRight listOf(1, 2, 3)
    }

    "list of either to either of list return the first left only if any" {
        // arrange
        val sample = listOf(1.right(), 2.right(), "e".left(), 3.right(), "b".left())
        // act
        val res = sample.toEitherOfList()
        // assert
        res shouldBeLeft "e"
    }

    "list of either to either of list with empty list" {
        // arrange
        val sample = emptyList<Either<Int, Boolean>>()
        // act
        val res = sample.toEitherOfList()
        // assert
        res shouldBeRight emptyList()
    }
})
