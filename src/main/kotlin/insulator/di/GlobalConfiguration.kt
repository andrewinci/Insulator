package insulator.di

import insulator.lib.configuration.model.Cluster

class GlobalConfiguration {
    companion object {
        var currentCluster: Cluster = Cluster.Empty
    }
}
