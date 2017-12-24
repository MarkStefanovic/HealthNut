package src.app

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val leftAlignedCell by cssclass()
        val rightAlignedCell by cssclass()
    }

    init {
        val defaultFont = mixin {
            fontSize = 12.px
            fontFamily = "Arial"
        }
        s(label, heading) {
            +defaultFont
            padding = box(5.px)
            fontSize = 14.px
            fontWeight = FontWeight.BOLD
        }
        leftAlignedCell {
            and(tableCell) {
                //            backgroundColor += Color.LIGHTGREY
                +defaultFont
                alignment = Pos.TOP_LEFT
            }
        }
        rightAlignedCell {
            and(tableCell) {
                //            backgroundColor += Color.LIGHTGREY
                +defaultFont
                alignment = Pos.TOP_RIGHT
            }
        }
        s(tableCell, heading) {
            +defaultFont
        }
    }
}