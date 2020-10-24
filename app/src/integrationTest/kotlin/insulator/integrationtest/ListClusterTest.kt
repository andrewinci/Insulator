package insulator.integrationtest

import io.kotest.core.spec.style.StringSpec

class ListClusterTest : StringSpec({

//    "Insulator start successfully showing the list of clusters" {
//        IntegrationTestContext(false).use {
//            // arrange
//            val cluster = Cluster.empty().copy(name = "Test cluster")
//            it.configureDi(
//                ConfigurationRepo::class to mockk<ConfigurationRepo> {
//                    every { addNewClusterCallback(any()) } just runs
//                    coEvery { getConfiguration() } returns
//                        Configuration(clusters = listOf(cluster)).right()
//                }
//            )
//
//            // act
//            it.startApp(Insulator::class.java)
//
//            // assert
//            (FX.primaryStage.scene.window as Stage).title shouldBe "Insulator"
//            FxAssert.verifyThat("#cluster-${cluster.guid} .label", LabeledMatchers.hasText(cluster.name))
//        }
//    }
})
