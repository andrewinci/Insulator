package insulator.views.update

import insulator.update.model.Release
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class UpdateInfoViewTest : StringSpec({

    "View is correctly rendered" {
        // arrange
        val sut = UpdateInfoView(Release("", "", "", "", ""))
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }
})
