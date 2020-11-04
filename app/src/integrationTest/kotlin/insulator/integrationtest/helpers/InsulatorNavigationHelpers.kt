package insulator.integrationtest.helpers

import insulator.kafka.model.Cluster
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import tornadofx.CssRule
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun selectCluster(cluster: Cluster) = getPrimaryWindow()
    .lookupFirst<Node>(CssRule.id("cluster-${cluster.guid}"))
    .doubleClick()

@ExperimentalTime
suspend fun Node.selectTopic(topicName: String) = lookupFirst<Label>(CssRule.id(topicName)).doubleClick()

@ExperimentalTime
suspend fun Node.startStopConsumer() = lookupFirst<Button>(CssRule.id("button-consume-stop")).click()
