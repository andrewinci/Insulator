package helper

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.test.TestCaseOrder

object ProjectConfig : AbstractProjectConfig() {
    override val parallelism = 1
    override val testCaseOrder = TestCaseOrder.Random
}