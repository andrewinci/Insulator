package insulator.viewmodel

import insulator.model.Cluster
import tornadofx.Controller

class ConfigurationsViewModel : Controller() {
    fun clusters(): List<Cluster>  = listOf(Cluster("Cluster name", "http://clusterurl.co.uk"))
}