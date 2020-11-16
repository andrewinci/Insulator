package insulator.helper

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.beans.property.SimpleStringProperty

class ObservableCollectionHelperKtTest : FreeSpec({

    "createListBindings" - {
        // arrange
        val prop = SimpleStringProperty("")
        val op = { prop.value.toCharArray().toList() }
        // act
        val observableCollection = createListBindings(op, prop)

        "on prop change, the observable collection changes as well 1" {
            val test = "test"
            prop.set(test)
            observableCollection.toList() shouldBe test.toCharArray().toList()
        }

        "on prop change, the observable collection changes as well 2" {
            val test = "another test"
            prop.set(test)
            observableCollection.toList() shouldBe test.toCharArray().toList()
        }
    }

    "createListBindings without base prop" {
        val testList = (1..10).toList()
        val observableCollection = createListBindings({ testList })
        observableCollection.toList() shouldBe testList
    }
})
