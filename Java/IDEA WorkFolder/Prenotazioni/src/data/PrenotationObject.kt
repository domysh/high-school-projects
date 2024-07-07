package data

import java.util.*
import kotlin.properties.Delegates

class PrenotationObject {


    var iDDonator: Int by Delegates.notNull()
    var access: Boolean by Delegates.notNull()
    var done: Boolean by Delegates.notNull()
    var note: String by Delegates.notNull()
    var day: Int by Delegates.notNull()
    var month: Int by Delegates.notNull()
    var year: Int by Delegates.notNull()
    var hInit: Int by Delegates.notNull()
    var hEnd: Int by Delegates.notNull()
    var internalPRID: String by Delegates.notNull()
    var name: String by Delegates.notNull()
    var surename: String by Delegates.notNull()
    var birth: Date by Delegates.notNull()
    var phone: String by Delegates.notNull()

    //ADDICTIONAL THINGS
    val prid: String
    val date: Date
    val dateNoH: Date


    fun getElementByKey(key: String): Any {
        when (key) {
            PftFile.IDDON_KEY -> return iDDonator
            PftFile.ACCESS_KEY -> return access
            PftFile.DONE_KEY -> return done
            PftFile.NOTE_KEY -> return note
            PftFile.DAY_KEY -> return day
            PftFile.INITH_KEY -> return hInit
            PftFile.ENDH_KEY -> return hEnd
            PftFile.IPRID_KEY -> return internalPRID
            PftFile.MONTH_KEY -> return month
            PftFile.YEAR_KEY -> return year
            PftFile.DONNAME_KEY -> return name
            PftFile.DONSURENAME_KEY -> return surename
            PftFile.DONBIRTH_KEY -> return birth
            PftFile.DONPHONE_KEY -> return phone
        }
        throw IllegalArgumentException(StaticVals.Generics.Errors.NOT_VALID_KEY)
    }

    override fun toString(): String {
        return prid
    }

    constructor(iDDonator_: Int,access_: Boolean, done_: Boolean, note_: String, day_: Int,
                month_: Int, year_: Int, hInit_: Int, hEnd_: Int, internalPRID_: String, name_: String?,
                surename_: String?, birth_: Date?, phone_: String?){
        iDDonator = iDDonator_;access = access_; done = done_;note = note_;day=day_;month = month_
        year = year_; hInit = hInit_; hEnd = hEnd_; internalPRID = internalPRID_
        prid = PftFile.getPRID(year, internalPRID, month)
        date = PftFile.getDateFrom(year, month, day, hInit, 0, 0)
        dateNoH = PftFile.getDateFrom(year, month, day, 0, 0, 0)
        if (iDDonator != -1) {
            name = DBManagment.self.getName(iDDonator)
            surename = DBManagment.self.getSurename(iDDonator)
            birth = DBManagment.self.getBirthDate(iDDonator)
            phone = DBManagment.self.getPhone(iDDonator)
        }else{
            name = name_!!
            surename = surename_!!
            birth = birth_!!
            phone = phone_!!
        }
    }
}