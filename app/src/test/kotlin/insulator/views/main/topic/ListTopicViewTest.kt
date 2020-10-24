package insulator.views.main.topic

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk

class ListTopicViewTest : StringSpec({

    "view renders correctly" {
        FxContext().use {
            ListTopicView(mockk(relaxed = true))
        }
    }
})
