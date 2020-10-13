package insulator.views.update

import insulator.lib.update.Release
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class UpdateInfoViewTest : FunSpec({
    test("View is correctly rendered") {
        // arrange
        val sut = UpdateInfoView(Release("", "", "", "", ""))
        // act
        val res = sut.root
        // assert
        res shouldNotBe null
    }
})
