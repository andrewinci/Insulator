package insulator.views.main.schemaregistry

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk

class ListSchemaViewTest : StringSpec({
    "view renders correctly" {
        FxContext().use {
            ListSchemaView(mockk(relaxed = true))
        }
    }
})
