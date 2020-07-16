package insulator.viewmodel

import insulator.lib.kafka.AdminApi
import tornadofx.*

class ListTopicViewModel : ViewModel() {
    private val adminApi: AdminApi by di()

    fun listTopics(): Collection<TopicViewModel> =
            adminApi.listTopics().map { it.map { topic -> TopicViewModel(topic) } }.unsafeRunSync()
}