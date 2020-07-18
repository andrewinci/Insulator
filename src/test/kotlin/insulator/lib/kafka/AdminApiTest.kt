package insulator.lib.kafka

import org.apache.kafka.clients.admin.AdminClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.apache.kafka.clients.admin.AdminClientConfig
import java.util.*

class AdminApiTest : StringSpec({
    "Test happy path"{
        with(AdminApiTestFixture()) {
            // arrange
            val adminClient = AdminClient.create(properties)
            val sut = AdminApi(adminClient, mockk())
            // act
            val result = sut.getOverview()
            // assert
            result shouldNotBe null
        }
    }
})

private class AdminApiTestFixture {
    val properties = Properties().apply {
        put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    }
}
