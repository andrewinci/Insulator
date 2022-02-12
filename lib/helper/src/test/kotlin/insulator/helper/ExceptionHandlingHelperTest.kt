package insulator.helper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

class ExceptionHandlingHelperTest : StringSpec({

    "runCatchingE return right if no exceptions are thrown" {
        // arrange
        val expectedResult = 1
        // act
        val res = runCatchingE { expectedResult }
        // assert
        res shouldBeRight expectedResult
    }

    "runCatchingE return left if an exception is thrown" {
        // arrange
        val testException = Throwable("An exception message")
        // act
        val res = runCatchingE { throw testException }
        // assert
        res shouldBeLeft testException
    }

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
