package insulator.lib.jsonhelper

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

class UUIDSerializerTest : StringSpec({

    val json = Json {}

    "descriptor" {
        // arrange
        val sut = UUIDSerializer()
        // act/assert
        sut.descriptor shouldNotBe null
    }

    "serialize" {
        // arrange
        val uuid = UUID.randomUUID()
        val testObject = Test(uuid)
        // act
        val res = json.encodeToString(Test.serializer(), testObject)
        // assert
        res shouldBe "{\"uuid\":\"$uuid\"}"
    }

    "deserialize" {
        // arrange
        val uuid = UUID.randomUUID()
        // act
        val res = json.decodeFromString(Test.serializer(), "{\"uuid\":\"$uuid\"}")
        // assert
        res shouldBe Test(uuid)
    }
})

@Serializable
data class Test(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID
)
