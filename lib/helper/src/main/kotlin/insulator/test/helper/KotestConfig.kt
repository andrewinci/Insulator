//@file:Suppress("unused")
//
//package insulator.test.helper
//
//import io.kotest.core.config.AbstractProjectConfig
//import io.kotest.core.test.TestCaseOrder
//
//object KotestConfig : AbstractProjectConfig() {
//    override val parallelism = 1
//    override val testCaseOrder = TestCaseOrder.Sequential // todo: only for integration
//    override fun afterAll() {
//        deleteTestSandboxFolder()
//        super.afterAll()
//    }
//}
