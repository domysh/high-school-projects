package backend


fun main() {
    // Set Graphics
    try {
        data.SettingsManager.stdCreationFile()
        MainFrame.setLookAndFeel()
        BugLogger.Logger.getInstance()
        MainFrame.start(graphics.MenuPane())
    } catch (e: Exception) {
        MainFrame.createJError("Errore generale!", e)
    }
}
