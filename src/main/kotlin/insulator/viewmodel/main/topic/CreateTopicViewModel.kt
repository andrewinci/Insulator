package insulator.viewmodel.main.topic

import arrow.core.Either
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

class CreateTopicViewModel(cluster: CreateTopicModel = CreateTopicModel()) : ItemViewModel<CreateTopicModel>(cluster) {

    val admin: AdminApi by di()

    val nameProperty = bind { item?.nameProperty }
    val partitionCountProperty = bind { item?.partitionCountProperty }
    val replicationFactorProperty = bind { item?.replicationFactorProperty }
    val isCompactedProperty = bind { item?.isCompactedProperty }

    fun save(): Either<Throwable, Unit> = admin.createTopics(this.item.toTopic()).get()
}

class CreateTopicModel(topic: Topic? = null) {
    val nameProperty = SimpleStringProperty(topic?.name)
    val partitionCountProperty = SimpleIntegerProperty(topic?.partitionCount ?: 0)
    val replicationFactorProperty = SimpleIntegerProperty(topic?.replicationFactor?.toInt() ?: 0)
    val isCompactedProperty = SimpleBooleanProperty(topic?.isCompacted ?: false)

    fun toTopic() = Topic(
        name = nameProperty.value,
        isInternal = null,
        partitionCount = partitionCountProperty.value,
        messageCount = null,
        replicationFactor = replicationFactorProperty.value.toShort(),
        isCompacted = isCompactedProperty.value
    )
}
