package insulator.integrationtest

import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.clickOkOnDialog
import insulator.integrationtest.helpers.eventually
import insulator.integrationtest.helpers.lookupAny
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.lookupWindowByTitle
import insulator.integrationtest.helpers.mainWindow
import insulator.kafka.model.Cluster
import insulator.ui.style.ButtonStyle.Companion.alertButton
import insulator.ui.style.ButtonStyle.Companion.settingsButton
import insulator.ui.style.TextStyle.Companion.h1
import insulator.ui.style.TextStyle.Companion.h2
import insulator.ui.style.TextStyle.Companion.subTitle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import tornadofx.CssRule
import tornadofx.Stylesheet.Companion.button
import tornadofx.Stylesheet.Companion.label
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ListClusterViewTests : FreeSpec({

    fun lookupClusterNode(cluster: Cluster) = mainWindow().lookupFirst<Node>(CssRule.id("cluster-${cluster.guid}"))

    "Happy path start the app and show list clusters view" - {
        FxFixture().use { fixture ->
            val clusters = (1..5).map { Cluster(name = "clusterName$it", endpoint = "endpoint$it") }.toTypedArray()
            fixture.startApp(*clusters)

            "Title should be Clusters" {
                eventually {
                    mainWindow()
                        .lookupFirst<Label>(h1).text shouldBe "Clusters"
                }
            }

            "All clusters config should be available with a settings button" {
                clusters.forEach {
                    with(lookupClusterNode(it)) {
                        lookupFirst<Label>(h2).text shouldBe it.name
                        lookupFirst<Label>(subTitle).text shouldBe it.endpoint
                        lookupFirst<Button>(settingsButton)
                    }
                }
            }
            // screenShoot()
        }
    }

    "Add a new cluster" {
        FxFixture().use { fixture ->
            fixture.startApp()
            val newClusterName = "New cluster name"
            val newEndpoint = "newEndpoint:8080"
            // Open the new cluster view
            mainWindow().lookupFirst<Button>(CssRule.c("button-bar").contains(button)).click()
            eventually {
                with(lookupWindowByTitle("New cluster")) {
                    lookupAny<Label>(h1).map { it.text } shouldContainAll listOf("Cluster connection", "Schema registry")
                }
            }
            // Create a new cluster with only name and endpoint
            eventually {
                with(lookupWindowByTitle("New cluster")) {
                    mapOf(
                        "field-cluster-name" to newClusterName,
                        "field-endpoint" to newEndpoint
                    ).forEach { (name, value) ->
                        lookupFirst<TextField>(CssRule.id(name)).textProperty().set(value)
                    }
                    lookupAny<Button>(button).first { it.text == "Save" }.click()
                }
            }
            // The new cluster is available in the list of clusters
            eventually {
                mainWindow().lookupAny<Label>(label).map { it.text } shouldContainAll listOf(newClusterName, newEndpoint)
            }
        }
    }

    "Delete a cluster" {
        FxFixture().use { fixture ->
            val cluster = Cluster(name = "clusterName", endpoint = "endpoint")
            fixture.startApp(cluster)
            // Open settings windows
            eventually {
                lookupClusterNode(cluster).lookupFirst<Button>(settingsButton).click()
            }
            // Click delete cluster button
            eventually { lookupWindowByTitle(cluster.name).lookupFirst<Button>(alertButton).click() }
            // Click OK on the dialog
            eventually { clickOkOnDialog() }
            // The cluster is deleted from the list of clusters"
            eventually {
                mainWindow().lookupAny<Label>(label).filter { it.text == cluster.name } shouldBe emptyList()
            }
        }
    }
})
