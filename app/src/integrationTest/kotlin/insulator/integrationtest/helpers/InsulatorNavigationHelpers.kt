package insulator.integrationtest.helpers

import insulator.kafka.model.Cluster
import javafx.scene.Node
import tornadofx.CssRule
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun selectCluster(cluster: Cluster) = getPrimaryWindow()
    .lookupFirst<Node>(CssRule.id("cluster-${cluster.guid}"))
    .doubleClick()