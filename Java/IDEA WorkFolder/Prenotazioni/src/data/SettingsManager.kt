package data

import backend.MainFrame
import org.json.JSONObject
import org.json.JSONTokener
import java.awt.Image
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import kotlin.properties.Delegates


//Le impostazioni saranno generate nella home, nella cartella .FratesP 
object SettingsManager {
    // Settings Variables
    private var pathToAcessDBInternal: String by Delegates.notNull()
    private var pathTofratespInternal: String by Delegates.notNull()
    private var dataFormatInternal: Int by Delegates.notNull()
    private var usePresettedDatesInternal: Boolean by Delegates.notNull()


    private var SLoaded = false

    fun getImageIconByImg(name: String): ImageIcon? {
        return try {
            ImageIcon(getImageByImg(name))
        } catch (e: Exception) {
            MainFrame.createJError("${StaticVals.Generics.Errors.LOADING_IMG}$name", e)
            null
        }
    }

    fun getImageByImg(name: String): Image? {
        return try {
            ImageIO.read(ClassLoader.getSystemResource("img/$name"))
        } catch (e: Exception) {
            MainFrame.createJError("${StaticVals.Generics.Errors.LOADING_IMG}$name", e)
            null
        }
    }

    // Caricamento da file delle variabili
    @Synchronized
    fun loadSetting() {
        if (!File(StaticVals.Settings.FILE_PATH).exists()) {
            stdCreationFile()
        }
        try {
            val reader = FileReader(StaticVals.Settings.FILE_PATH)
            val data = JSONObject(JSONTokener(reader))
            pathToAcessDBInternal = data.getString(StaticVals.Settings.Json.DB)
            pathTofratespInternal = data.getString(StaticVals.Settings.Json.PFT)
            dataFormatInternal = data.getInt(StaticVals.Settings.Json.DATA_FORMAT)
            usePresettedDatesInternal = data.getBoolean(StaticVals.Settings.Json.PRESETTED_DATES)
            SLoaded = true
            reader.close()
        } catch (e: Exception) {
            resetSettings()
            MainFrame.noticePane(StaticVals.Generics.SETTINGS_RELOADED)
        }
    }

    private fun loaded() {
        if (!SLoaded) {
            loadSetting()
        }
    }

    // Get delle Settings + controlo del load delle Settings
    var pathAccessDB: String
        get() {
            loaded()
            return pathToAcessDBInternal
        }
        // Modifico le impostazioni (anche su file)
        set(value) {
            loaded()
            pathToAcessDBInternal = value
            creationFile()
            DBManagment.unload()
        }

    var pathToFratesP: String
        get() {
            loaded()
            return pathTofratespInternal
        }
        set(Path) {
            loaded()
            pathTofratespInternal = Path
            creationFile()
            PftFile.loadData()
        }

    var dataFormat: String
        get() {
            return StaticVals.Settings.DATA_FORMATS[dataFormatInternal]
        }
        set(value){
            dataFormatInt = StaticVals.Settings.DATA_FORMATS.indexOf(value)
        }

    var dataFormatInt: Int
        get(){
            loaded()

            if (dataFormatInternal >= 0 && StaticVals.Settings.DATA_FORMATS.size> dataFormatInternal)
                return dataFormatInternal
            else
                dataFormatInt = 0
            return 0
        }
        set(value){
            loaded()
            dataFormatInternal = if (value >= 0 && StaticVals.Settings.DATA_FORMATS.size> value) value else 0
            creationFile()
            PftFile.loadData()
        }

    var usePresettedDates:Boolean
        get(){
            loaded()
            return usePresettedDatesInternal
        }
        set(value){
            loaded()
            usePresettedDatesInternal = value
            creationFile()
            //Update Effective value in the system
        }


    //creation file deve aggiornare le informazioni
    // Funzione che crea la path .FratesP e scrive il file di impostazioni di
    // default
    @Synchronized
    private fun creationFile(overwrite: Boolean) {
        File( StaticVals.Path.MAIN_DIR).mkdirs()
        try {
            var fw = FileWriter( StaticVals.joinPath(StaticVals.Path.MAIN_DIR, StaticVals.Path.README_FILENAME))
            fw.write(StaticVals.Info.NO_TOUCH_MESSAGE)
            fw.close()
            if (!File(StaticVals.Settings.FILE_PATH).exists() || overwrite) {
                val data = JSONObject()
                data.put(StaticVals.Settings.Json.DB, pathToAcessDBInternal)
                data.put(StaticVals.Settings.Json.PFT,pathTofratespInternal)
                data.put(StaticVals.Settings.Json.DATA_FORMAT, dataFormatInternal)
                data.put(StaticVals.Settings.Json.PRESETTED_DATES, usePresettedDatesInternal)
                fw = FileWriter(StaticVals.Settings.FILE_PATH)
                fw.write(data.toString(4))
                fw.close()
            }
            if ( pathToFratesP == StaticVals.Path.PTF_FILE_PATH && !File(pathTofratespInternal).exists() ) {
                PftFile.stdFileCreation()
            }
        } catch (e: IOException) {
            MainFrame.createJError(StaticVals.Generics.Errors.SETTINGS_GENERATION, e)
        }
    }

    private fun creationFile() {
        creationFile(true)
    }

    fun stdValues() {
        pathToAcessDBInternal =  StaticVals.Path.DB_FILE_PATH
        pathTofratespInternal =  StaticVals.Path.PTF_FILE_PATH
        dataFormatInternal = 0
        usePresettedDatesInternal = StaticVals.Settings.USE_PRESETED_DATES
    }

    fun stdCreationFile() {
        stdValues()
        creationFile(false)
        loadSetting()
    }

    fun resetSettings() {
        stdValues()
        creationFile(true)
    }
}