package data
import java.nio.file.Paths

object StaticVals {

    object Dimensions {

        val ErrorFrame = Dim(400,250)
        val ServiceFrame = Dim(350,160)
        val MainFrame = Dim(100,100)
        val NewDonation = Dim(680,560)

    }
    data class Dim(val width: Int,val height: Int)

    object Generics{
        val PRID_SIZE = 10
        val NAME_MAIN_FRAME = "Prenotazioni Fratres"
        val NONE_TEXT = "None"
        val IMPOSSIBLE = "Non disponibile"
        val NOT_VALID = "non è valido!"
        val NO_PRENOTATION = "Non ci sono prenotazioni!"
        val WANT_EXIT = "Sei sicuro di uscire?"
        val LOADING = "Caricamento"
        val SETTINGS_RELOADED = "Ci è stato un problema con la lettura delle Impostazioni... Le impostazioni sono state rigenerate."
        val LOADING_DONE = "Caricamento completato."
        val ONLY_AGE = "SOLO ETÀ"
        val NO_NOTE = "Nessuna Nota!"
        val PRENOTATION_MODIFIED = "La prenotazione è stata modificata"
        val DATE_NOT_PERMITTED = "Donazione non permessa nella data!"
        val PRENOTATION_NOT_MODIFIED = "La prenotazione non è stata modificata!"
        val SURE_TO_IGNORE = "Sei sicuro di voler annullare le modifiche?"
        val SURE_TO_DELETE_PRENOTATION = "Sei sicuro di voler eliminare questa prenotazione?"
        val WANT_SAVE_FIRST = "Prima di uscire vuoi salvare?"
        val ILLEGAL_AGE = "Non hai una età adeguata!"
        val DATE_CONFLICT = "Data in conflitto con altre prenotazioni!"
        val TO_SAVE = "DA SALVARE"
        val SAVED = "SALVATO!"
        val SAVE = "Salva"
        val SAVE_STATE = "SALVATAGGIO:"
        val NEXT_PRENOTATION = "PROSSIMA PRENOTAZIONE:"
        val SAVED_PHONE = "TELEFONO SALVATO:"
        val LAST_DONATION = "ULTIMA DONAZIONE:"
        val AGREE = "IDONEO:"
        val FROM = "DAL:"
        val SEARCH_WRITING_DOWN = "Cerca il donatore desiderato scrivendo qui sotto"
        val HOME = "Home"
        val COMPLETE_PARAMETHERS = "Completare tutti i parametri"
        val DATE_FORMAT_INPUT = "Formato inserimento Date:"
        object Errors{
            val PRENOTATION_WRITE = "Errore nella modifica della prenotazione!"
            val LOADING_FILE = "Errore nel caricamento di:"
            val ERROR_TITLE = "Si è verificato un problema!"
            val SUB_TITLE_ERROR = "Ops! C'è stato un problema! :("
            val CONTACT_PREFIX = "Si prega di scrivere a "
            val GRAPHICS_STYLE = "Errore nel settaggio della grafica"
            val WRITING_PFT = "Errore nella scritturadel file pft"
            val NOT_VALID_KEY = "INCOMPATIBLE KEY!"
            val LOADING_IMG = "Error loading image in img/"
            val SETTINGS_GENERATION = "Errore nel creare il file delle impostazioni"
        }
        object ActionMouse{
            val UNDO = "Annulla"
            val CUT = "Taglia"
            val COPY = "Copia"
            val PASTE = "Incolla"
            val SELECT_ALL = "Seleziona Tutto"
        }
        object Menu{
            val NEW_PRENOTATION = "Nuova prenotazione"
            val LIST = "Elenco"
            val DATA_MANAGE = "Gestione dati"
            val STATS = "Statistiche"
            val SETTINGS = "Impostazioni"
        }
    }

    object Img{
        val LOGO = "logo.png"
        val PERSON = "personS.png"
        val BLOOD_ICON = "bloodS.png"
        val SAVE_FLOPPY = "floppySS.png"
        val DELETE_CROSS = "deleteSS.png"
        val GO_BACK = "backSS.png"
        val TREE_LOGO = "logo2.png"
    }

    object Path{
        val README_FILENAME = "README.txt"
        val DIR_NAME = ".FratresP"
        val HOME_DIR : String = System.getProperty("user.home")
        val MAIN_DIR = joinPath(HOME_DIR, DIR_NAME)

        val DB_FILE_PATH = joinPath(MAIN_DIR, Settings.DB_FILE_NAME)
        val PTF_FILE_PATH =  joinPath(MAIN_DIR, Settings.PTF_FILE_NAME)

        object LogFolder {
            val LOG_DIR_NAME = "log"
            val FILE_EXTENTION = ".log"
            val FILE_PREFIX_ERROUT = "errout_"
            val LOG_DIR = joinPath(MAIN_DIR, LOG_DIR_NAME)
            val FILE_PREFIX_LOG = "log_"
        }

    }

    object Settings{
        val FILE_NAME = "settings.edit"
        val DB_FILE_NAME = "db.mdb"
        val PTF_FILE_NAME = "prenotazioni.pft"
        val DATA_FORMATS = arrayOf("dd MMM yyyy","dd-MMM-yyyy","dd/MMM/yyyy","dd MM yyyy","dd/MM/yyyy","dd-MM-yyyy","dd.MM.yyyy")
        val DAY_LIMIT = 90
        val MIN_AGE_LIMIT = 18
        val MAX_AGE_LIMIT = 65
        val USE_PRESETED_DATES = true
        val PFT_FILE_ENCODING = "UTF-8"
        val FILE_PATH = joinPath(Path.MAIN_DIR, FILE_NAME)
        object Json {
            val DB = "DB"
            val PFT = "PFT"
            val DATA_FORMAT = "DATA"
            val DAY_LIMIT = "DAY_LIMIT"
            val PRESETTED_DATES = "PRESET_DATES"
        }
    }


    object Info {
        object DB{
            val TABLE_DONATORI = "DONATORI"
            val TABLE_DONAZIONI = "DONAZIONI"
        }
        val VERSION = "1.2.3 Beta"
        object Contact {
            val EMAIL = "me@domysh.com"
            val BUGEMAIL = "admin@domysh.com"
            val TELEGRAM = "@DomySh"
        }
        val NO_TOUCH_MESSAGE ="I FILE IN QUESTA CARTELLA SONO GENERATI I FILE IMPOSTAZIONI DEL GESTIONALE\n" +
                "SE QUESTA CARTELLA VERRA' ELIMINATA, VERRA' RICREATA AL SUCCESSIVO AVVIO DEL PROGRAMMA.\n" +
                "ATTENZIONE! QUESTA CARTELLA CONTIENE DATI UTILI AL FUNZIONAMENTO DEL PROGRAMMA\n\n" +
                "Telegram: ${Contact.TELEGRAM}; Mail: ${Contact.BUGEMAIL}"
    }

    fun joinPath( path1 : String,path2: String ):String = Paths.get(path1,path2).toString()
}

