package insulator

import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JOptionPane

class BootstrapViewManager(private val frame: JFrame) {

    private var updateView: UpdateView? = null

    fun updateDownloadProgress(fraction: Float) {
        updateView?.updateDownloadProgress(fraction)
    }

    fun showMessageDialog(message: String, title: String, iconId: Int) = JOptionPane.showMessageDialog(frame, message, title, iconId)

    fun closeUpdateView() {
        frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    fun showUpdateView() {
        updateView = UpdateView(frame)
    }
}
