package insulator.lib.kafka

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk

class SchemaRegistryTest : StringSpec({

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
})
