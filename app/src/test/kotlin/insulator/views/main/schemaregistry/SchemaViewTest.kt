package insulator.views.main.schemaregistry

import helper.FxContext
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk

class SchemaViewTest : StringSpec({
    "view renders correctly" {
        FxContext().use {
            SchemaView(mockk(relaxed = true))
        }
    }
})
