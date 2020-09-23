package insulator.lib.update

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VersionCheckerTest : FunSpec({

    test("getCurrentVersion") {

    }

    test("getLatestVersion") {
        // arrange
        val sut = VersionChecker()
        // act
        val latestVersion = sut.getLatestVersion()
        // assert
        latestVersion shouldBeRight Release("0.0.10", "", "", "", "")
    }
})
