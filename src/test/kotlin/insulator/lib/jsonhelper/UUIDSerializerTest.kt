package insulator.lib.jsonhelper

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.UUID

class UUIDSerializerTest : FunSpec({

    val json = Json(JsonConfiguration.Stable)

    test("descriptor") {
        // arrange
        val sut = UUIDSerializer()
        // act/assert
        sut.descriptor shouldNotBe null
    }

    test("serialize") {
        // arrange
        val uuid = UUID.randomUUID()
        val testObject = Test(uuid)
        // act
        val res = json.stringify(Test.serializer(), testObject)
        // assert
        res shouldBe "{\"uuid\":\"$uuid\"}"
    }

    test("deserialize") {
        // arrange
        val uuid = UUID.randomUUID()
        // act
        val res = json.parse(Test.serializer(), "{\"uuid\":\"$uuid\"}")
        // assert
        res shouldBe Test(uuid)
    }
})

@Serializable
data class Test(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID
)
