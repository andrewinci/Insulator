package insulator.kafka

import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import io.confluent.kafka.schemaregistry.ParsedSchema
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class SchemaRegistryTest : StringSpec({

    "happy path build schema registry" {
        // arrange
        val cluster = Cluster(
            name = "name",
            endpoint = "endpoint",
            schemaRegistryConfig = SchemaRegistryConfiguration(
                "endpoint",
                "username",
                "password"
            )
        )
        // act
        val res = schemaRegistry(cluster)
        // assert
        res shouldNotBe null
    }

    "happy path getAllSubjects" {
        // arrange
        val subjects = listOf("subject1", "subject2")
        val mockSchema = mockk<SchemaRegistryClient> {
            every { allSubjects } returns subjects
        }
        val sut = SchemaRegistry(mockSchema)
        // act
        val res = sut.getAllSubjects()
        // assert
        res shouldBeRight subjects
    }

    "happy path getSubject" {
        // arrange
        val mockSchema = mockk<SchemaRegistryClient> {
            every { getAllVersions(any()) } returns listOf(1, 2, 3)
            every { getByVersion(any(), any(), any()) } returns
                mockk {
                    every { schema } returns "asd"
                    every { version } returns 1
                    every { id } returns 123
                }
        }
        val sut = SchemaRegistry(mockSchema)
        // act
        val res = sut.getSubject("subject1")
        // assert
        res shouldBeRight { }
    }

    "happy path deleteSubject" {
        // arrange
        val mockSchema = mockk<SchemaRegistryClient> {
            every { deleteSubject(any()) } returns listOf(1)
        }
        val sut = SchemaRegistry(mockSchema)
        // act
        val res = sut.deleteSubject("subject1")
        // assert
        res shouldBeRight { }
    }

    "happy path deleteSchemaVersion" {
        // arrange
        val mockSchema = mockk<SchemaRegistryClient> {
            every { deleteSchemaVersion(any(), any()) } returns 1
        }
        val sut = SchemaRegistry(mockSchema)
        // act
        val res = sut.deleteSchemaVersion("subject1", 1)
        // assert
        res shouldBeRight { }
    }

    "register an invalid schema return left" {
        // arrange
        val mockSchema = mockk<SchemaRegistryClient>()
        val sut = SchemaRegistry(mockSchema)
        // act
        val res = sut.register("test", "{}")
        // assert
        res shouldBeLeft {}
    }

    "register a valid schema return true if accepted by the schema registry" {
        // arrange
        val mockSchema = mockk<SchemaRegistryClient> {
            every { register(any(), any<ParsedSchema>()) } returns 1
        }
        val sut = SchemaRegistry(mockSchema)
        val testSchema =
            """
            { "type": "record", "namespace": "com.example", "name": "FullName", "fields": [{ "name": "first", "type": "string" }] } 
            """.trimIndent()
        // act
        val res = sut.register("test", testSchema)
        // assert
        res shouldBeRight {}
    }
})
