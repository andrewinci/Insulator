package insulator.views.main.topic

import helper.cleanupFXFramework
import helper.configureFXFramework
import helper.configureScopeDi
import insulator.viewmodel.main.topic.ProducerViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class ProducerViewTest : FunSpec({

    test("Render view without exceptions") {
        // arrange
        val sut = ProducerView()
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }

    beforeTest {
        configureFXFramework()
        configureScopeDi(
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
    }

    afterTest {
        cleanupFXFramework()
    }
})
