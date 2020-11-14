package insulator

import java.awt.Component
import java.awt.Dimension
import java.awt.Image
import java.awt.RenderingHints
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.math.floor

class UpdateView(private val frame: JFrame) {
    private val progressBar = JProgressBar().apply { isStringPainted = true }

    init {
        with(frame) {
            add(
                JPanel().apply {
                    add(insulatorIcon())
                    add(progressBar)

                    layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                    border = BorderFactory.createEmptyBorder(10, 20, 20, 20)
                }
            )
            fixSize(300, 130)
            center()
            isVisible = true
        }
    }

    fun updateDownloadProgress(frac: Float) =
        with(progressBar) {
            value = floor(frac.toDouble() * 100).toInt()
            if (value >= 98) frame.isVisible = false
        }

    private fun insulatorIcon() =
        ImageIO.read(this.javaClass.getResource("/icon.png"))
            .let { JLabel(ImageIcon(getScaledImage(it, 60, 60))).apply { text = "Insulator auto-update" } }
            .also { it.alignmentX = Component.CENTER_ALIGNMENT }

    private fun getScaledImage(srcImg: Image, width: Int, height: Int) =
        BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
            with(createGraphics()) {
                setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                drawImage(srcImg, 0, 0, width, height, null)
                dispose()
            }
        }

    private fun JFrame.center() = apply {
        val screen = Toolkit.getDefaultToolkit().screenSize
        setLocation(screen.width / 2 - size.width / 2, screen.height / 2 - size.height / 2)
    }

    private fun JFrame.fixSize(width: Int, height: Int) = this.apply {
        setSize(width, height)
        maximumSize = Dimension(width, height)
    }
}