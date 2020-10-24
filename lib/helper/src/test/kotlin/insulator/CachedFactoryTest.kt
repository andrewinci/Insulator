package insulator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class CachedFactoryTest : StringSpec({

    "happy path - factory call the provided builder and cache" {
        // arrange
        val expected = "test_result"
        val op = fun(_: String) = expected
        val obj = object : CachedFactory<String, String>(op) {}
        // act
        val res = obj.build("test")
        val res2 = obj.build("test")
        // assert
        res shouldBe expected
        res2 shouldBeSameInstanceAs res
    }
})
