package insulator.views.main.topic

import helper.FxContext
import insulator.viewmodel.main.topic.ProducerViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class ProducerViewTest : StringSpec({

    "Render view without exceptions" {
        FxContext().use {
            // arrange
            it.setup()
            val sut = ProducerView()
            // act
            val res = sut.root
            // assert
            res shouldNotBe null
        }
    }
})

private fun FxContext.setup() = this.configureFxDi(
    mockk<ProducerViewModel>(relaxed = true) {
        every { producerTypeProperty } returns SimpleObjectProperty()
        every { nextFieldProperty } returns SimpleStringProperty("")
        every { validationErrorProperty } returns SimpleStringProperty(null)
        every { keyProperty } returns SimpleStringProperty()
        every { valueProperty } returns SimpleStringProperty()
        every { canSendProperty } returns SimpleBooleanProperty()
        every { error } returns SimpleObjectProperty<Throwable?>(null)
    }
)
