package insulator.views.configurations

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk

class ListClusterViewTest : StringSpec({
    "view renders correctly" {
        FxContext().use {
            ListClusterView(mockk(relaxed = true), mockk(relaxed = true))
        }
    }
})
