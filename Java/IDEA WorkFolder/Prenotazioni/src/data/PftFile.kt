package data

import backend.MainFrame.createJError
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONTokener
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object PftFile {
    val isLoaded:Boolean
        get(){
            return (data!=null)
        }
    var error = "NULL"
        private set
    private var data: JSONObject? = null
    val IDDON_KEY = "IdDon"
    val ACCESS_KEY = "Access"
    val DONE_KEY = "Done"
    val NOTE_KEY = "Note"
    val DAY_KEY = "Day"
    val INITH_KEY = "InitH"
    val ENDH_KEY = "EndH"
    val IPRID_KEY = "Iprid"
    val MONTH_KEY = "Mounth"
    val YEAR_KEY = "Year"
    val GLOBAL_INFO_KEY = "INFO"
    val GLOBAL_INFO_MAX_KEY = "MAX"
    val GLOBAL_INFO_MIN_KEY = "MIN"
    val FILE_HEADER = "PFT_FILE"
    val GLOBAL_PRENOTATION_DATES = "DATES_PRENOT" // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    val DON_KEY = "don"
    val DONNAME_KEY = "name"
    val DONSURENAME_KEY = "suren"
    val DONBIRTH_KEY = "birth"
    val DONPHONE_KEY = "phone"

	val NEXT_DAY_FORM_DONATION: Int = StaticVals.Settings.DAY_LIMIT // devono passare 90 giorni dalla donazione di standard
    val GLOBAL_INFO_NULL_VALUE = -1
    val MIN_AGE_LIMIT = StaticVals.Settings.MIN_AGE_LIMIT
    val MAX_AGE_LIMIT = StaticVals.Settings.MAX_AGE_LIMIT
    val NULL : Array<PrenotationObject> = arrayOf()

    @Synchronized
    fun loadData() {
        try {
            val input = FileInputStream(SettingsManager.pathToFratesP)
            val control = input.readNBytes(FILE_HEADER.toByteArray().size)
            if (!control!!.contentEquals(FILE_HEADER.toByteArray())) throw Exception("Not Valid PFT File")
            val reader = InputStreamReader(GZIPInputStream(input), StaticVals.Settings.PFT_FILE_ENCODING)
            val tokener = JSONTokener(reader)
            data = JSONObject(tokener)
            reader.close()
            input.close()
        } catch (e: Exception) {
            error = e.toString()
            data = null
        }
    }

    private fun stdLoad() {
        if (!isLoaded) loadData()
    }

    fun stdFileCreation(){
        data = JSONObject()
        val infoObj = JSONObject()
        infoObj.put(GLOBAL_INFO_MAX_KEY, GLOBAL_INFO_NULL_VALUE)
        infoObj.put(GLOBAL_INFO_MIN_KEY, GLOBAL_INFO_NULL_VALUE)
        data!!.put(GLOBAL_INFO_KEY,infoObj)
        writeFile()

    }


    @Synchronized
    private fun writeFile() {
        if (!isLoaded) return

        try {
            val output = FileOutputStream(SettingsManager.pathToFratesP)
            output.write(FILE_HEADER.toByteArray())
            val writer = OutputStreamWriter(GZIPOutputStream(output), StaticVals.Settings.PFT_FILE_ENCODING)
            data!!.write(writer)
            writer.close()
            output.close()

        } catch (e: Exception) {
            error = e.toString()
            createJError(StaticVals.Generics.Errors.WRITING_PFT, e)
        }
    }

    fun pridExist(month: Int, year: Int, key: String?): Boolean {
        stdLoad()
        var section: JSONObject
        if (findJSONObject(data, year.toString())) {
            section = findOrCreateJSONObjet(data, year.toString())
            if (findJSONObject(section, month.toString())) {
                section = findOrCreateJSONObjet(section, month.toString())
                if (findJSONObject(section, key)) {
                    return true
                }
            }
        }
        return false
    }

    fun pridExist(PRID: String?): Boolean {
        return pridExist(getMonthFromPRID(PRID), getYearFromPRID(PRID), getInternalFromPRID(PRID))
    }

    fun getPRID(year: Int, InternalPRID: String, month: Int): String {
        var generatedKey = year.toString() + InternalPRID
        if (month < 10) {
            generatedKey += "0"
        }
        generatedKey += month
        return generatedKey
    }

    fun getPRID(d: Date, InternalPRID: String): String {
        return getPRID(getYear(d), InternalPRID, getMonth(d))
    }

    fun genPRID(month: Int, year: Int): String {
        var generatedKey = ""
        var generatedChar: Char
        while (true) {
            for (i in 0..3) {
                val rnd = Random()
                generatedChar = (rnd.nextInt(26) + 'A'.toInt()).toChar()
                generatedKey += generatedChar
            }
            if (!pridExist(month, year, generatedKey)) {
                return getPRID(year, generatedKey, month)
            }
        }
    }

    private fun findJSONObject(from: JSONObject?, key: String?): Boolean {
        return try {
            from!!.getJSONObject(key)
            true
        } catch (e: JSONException) {
            false
        }
    }

    private fun findOrCreateJSONObjet(from: JSONObject?, key: String): JSONObject {
        var res: JSONObject
        try {
            res = from!!.getJSONObject(key)
        } catch (e: JSONException) {
            res = JSONObject()
            from!!.put(key, res)
        }
        if (from === data && key !== GLOBAL_INFO_KEY) {
            val max = infoMaxYear
            val min = infoMinYear
            val thisData = try {
                key.toInt()
            } catch (e: NumberFormatException) {
                GLOBAL_INFO_NULL_VALUE
            }
            if (thisData != GLOBAL_INFO_NULL_VALUE) {
                if (max == GLOBAL_INFO_NULL_VALUE || min == GLOBAL_INFO_NULL_VALUE) {
                    infoMaxYear = thisData
                    infoMinYear = thisData
                } else {
                    if (thisData > max) infoMaxYear = thisData
                    if (thisData < min) infoMinYear = thisData
                }
            }
        }
        return res
    }

    var infoMaxYear: Int
        get() {
            stdLoad()
            val INFO = findOrCreateJSONObjet(data, GLOBAL_INFO_KEY)
            return INFO.getInt(GLOBAL_INFO_MAX_KEY)
        }
        set(toSet) {
            stdLoad()
            data!!.getJSONObject(GLOBAL_INFO_KEY).put(GLOBAL_INFO_MAX_KEY, toSet)
            writeFile()
        }

    var infoMinYear: Int
        get() {
            stdLoad()
            val INFO = findOrCreateJSONObjet(data, GLOBAL_INFO_KEY)
            return INFO.getInt(GLOBAL_INFO_MIN_KEY)
        }
        set(toSet) {
            stdLoad()
            data!!.getJSONObject(GLOBAL_INFO_KEY).put(GLOBAL_INFO_MIN_KEY, toSet)
            writeFile()
        }

    //PRENOTATION DATE PART
    private val prenotationDates: JSONObject
        get() {
            stdLoad()
            val RES = data!!.getJSONObject(GLOBAL_INFO_KEY)
            if (!findJSONObject(RES, GLOBAL_PRENOTATION_DATES)) {
                RES.put(GLOBAL_PRENOTATION_DATES, JSONObject())
                writeFile()
            }
            return RES.getJSONObject(GLOBAL_PRENOTATION_DATES)
        }

    fun addPrenotationDate(d: Date) {
        stdLoad()
        val o = prenotationDates
        val y: JSONArray
        val year = "" + getYear(d)
        y = try {
            o.getJSONArray(year)
        } catch (e: JSONException) {
            o.put(year, JSONArray())
            o.getJSONArray(year)
        }
        y.put(getMonth(d).toString() + "/" + getDay(d))
        writeFile()
    }

    fun getYearPrenotations(y: Int): Array<Date?>? {
        stdLoad()
        val o = prenotationDates
        return try {
            val a = o.getJSONArray("" + y)
            val res = arrayOfNulls<Date>(a.toList().size)
            for (i in res.indices) {
                val month = a.getString(i).split("/".toRegex()).toTypedArray()[0].toInt()
                val day = a.getString(i).split("/".toRegex()).toTypedArray()[1].toInt()
                res[i] = getDateFrom(y, month, day)
            }
            res
        } catch (e: JSONException) {
            null
        }
    }

    val prenotationYearList: IntArray
        get() {
            stdLoad()
            val o = prenotationDates
            val y = Vector<String>()
            val iter = o.keys()
            while (iter.hasNext()) {
                y.add(iter.next())
            }
            val res = IntArray(y.size)
            for (i in res.indices) {
                res[i] = y[i].toInt()
            }
            Arrays.sort(res)
            return res
        }

    fun removePrenotationYear(y: Int) {
        stdLoad()
        val o = prenotationDates
        try {
            o.remove(y.toString() + "")
            writeFile()
        } catch (e: JSONException) {
        }
    }

    fun removePrenotationDate(y: Int, m: Int, d: Int) {
        stdLoad()
        val o = prenotationDates
        try {
            val arr = o.getJSONArray(y.toString() + "")
            for (i in arr.toList().indices) {
                val sepDates = arr.getString(i).split("/".toRegex()).toTypedArray()
                if (sepDates[0].toInt() == m) {
                    if (sepDates[1].toInt() == d) {
                        arr.remove(i)
                        writeFile()
                        return
                    }
                }
            }
        } catch (e: JSONException) {
        }
    }

    //END PRENOTATION DATE PART
	fun addNewDataMap(PR: PrenotationObject): Boolean {
        stdLoad()
        var section: JSONObject
        section = findOrCreateJSONObjet(data, PR.year.toString())
        section = findOrCreateJSONObjet(section, PR.month.toString())
        if (!pridExist(PR.month, PR.year, PR.internalPRID)) {
            section = findOrCreateJSONObjet(section, PR.internalPRID)
            section.put(IDDON_KEY, PR.iDDonator)
            section.put(ACCESS_KEY, PR.access)
            section.put(DONE_KEY, PR.done)
            section.put(NOTE_KEY, PR.note)
            section.put(DAY_KEY, PR.day)
            section.put(INITH_KEY, PR.hInit)
            section.put(ENDH_KEY, PR.hEnd)
            if (PR.iDDonator == -1) {
                section = findOrCreateJSONObjet(section, DON_KEY)
                section.put(DONNAME_KEY, PR.name)
                section.put(DONSURENAME_KEY, PR.surename)
                section.put(DONBIRTH_KEY, fromDateToString(PR.birth))
                section.put(DONPHONE_KEY, PR.phone)
            }
            writeFile()
            return true
        }
        return false
    }

    fun deletePR(PRID: String?): Boolean {
        val YEAR = getYearFromPRID(PRID)
        val MONTH = getMonthFromPRID(PRID)
        val INTERNAL = getInternalFromPRID(PRID)
        try {
            data!!.getJSONObject(YEAR.toString()).getJSONObject(MONTH.toString()).remove(INTERNAL)
        } catch (e: JSONException) {
            loadData()
            return false
        }
        writeFile()
        return true
    }

    fun deletePR(PR: PrenotationObject): Boolean {
        return deletePR(PR.prid)
    }

    fun modifyPR(PRIDToMod: String, newPR: PrenotationObject): Boolean {
        if (!PRIDToMod.contentEquals(newPR.prid)) {
            if (!pridExist(newPR.prid)) return false
        }
        return if (!deletePR(PRIDToMod)) false else addNewDataMap(newPR)
    }

    fun modifyPR(PRToMod: PrenotationObject, newPR: PrenotationObject): Boolean {
        return modifyPR(PRToMod.prid, newPR)
    }

    private fun isNumber(l: Char): Boolean {
        val ascii = l.toInt()
        return ascii <= '9'.toInt() && ascii >= '0'.toInt()
    }

    fun getYearFromPRID(CompletePRID: String?): Int {
        return if (CompletePRID != null) {
            var resTmp = ""
            for (i in CompletePRID.indices) {
                resTmp += if (isNumber(CompletePRID[i])) {
                    CompletePRID[i]
                } else {
                    break
                }
            }
            try {
                resTmp.toInt()
            } catch (e: NumberFormatException) {
                -1
            }
        } else {
            -1
        }
    }

    fun getMonthFromPRID(CompletePRID: String?): Int {
        return if (CompletePRID != null) {
            var resTmp = ""
            for (i in CompletePRID.length - 1 downTo 0) {
                resTmp = if (isNumber(CompletePRID[i])) {
                    CompletePRID[i].toString() + resTmp
                } else {
                    break
                }
            }
            try {
                resTmp.toInt()
            } catch (e: NumberFormatException) {
                -1
            }
        } else {
            -1
        }
    }

	fun getInternalFromPRID(CompletePRID: String?): String? {
        return if (CompletePRID != null) {
            var resTmp = ""
            for (i in CompletePRID.indices) {
                if (!isNumber(CompletePRID[i])) {
                    resTmp += CompletePRID[i]
                }
            }
            resTmp
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun searcher(year: Int, month: Int, InternalPRID: String?): Array<PrenotationObject> {
        stdLoad()
        var resJSON: JSONObject
        val res = Vector<PrenotationObject>()
        resJSON = try {
            data!![year.toString()] as JSONObject
        } catch (e: JSONException) {
            return NULL
        }
        if (month != -1) {
            resJSON = try {
                JSONObject().put(month.toString(), resJSON[month.toString()])
            } catch (e: JSONException) {
                return NULL
            }
        }
        if (InternalPRID !== "") {
            resJSON = try {
                JSONObject().put(month.toString(),
                        JSONObject().put(InternalPRID, resJSON.getJSONObject(month.toString())[InternalPRID]))
            } catch (e: JSONException) {
                return NULL
            }
        }
        resJSON.toMap().forEach { (str, obj) ->
            (obj as HashMap<String?, Any>).forEach { (t: String?, u: Any) ->
                val ID = (u as HashMap<String?, Any?>)[IDDON_KEY] as Int

                // Gestisco il caso di ID == -1
                var name: String? = null
                var surename: String? = null
                var birth: String? = null
                var phone: String? = null
                if (ID == -1) {
                    val donInfo = u[DON_KEY] as HashMap<String, String>?
                    name = donInfo!![DONNAME_KEY]
                    surename = donInfo[DONSURENAME_KEY]
                    birth = donInfo[DONBIRTH_KEY]
                    phone = donInfo[DONPHONE_KEY]
                }
                // Creo la prenotazione sotto forma di Array
                val ele = PrenotationObject(ID, u[ACCESS_KEY] as Boolean,
                        u[DONE_KEY] as Boolean,
                        u[NOTE_KEY] as String,
                        u[DAY_KEY] as Int, str.toInt(), year,
                        u[INITH_KEY] as Int,
                        u[ENDH_KEY] as Int, t!!, name, surename, fromStringToDate(birth), phone)
                res.add(ele)
            }
        }
        return res.toTypedArray()
    }

    fun searchWithReference(year: Int, month: Int, InternalPRID: String): Array<PrenotationObject> {
        return searcher(year, month, InternalPRID)
    }


    fun searchWithDate(year: Int, month: Int = -1): Array<PrenotationObject> {
        return searcher(year, month, "")
    }

    fun searchWithDate(data: Date, InternalPRID: String = ""): Array<PrenotationObject> {
        return searcher(getYear(data), getMonth(data), InternalPRID)
    }

    fun serachWithPRID(PRID: String): Array<PrenotationObject> {
        return searcher(getYearFromPRID(PRID), getMonthFromPRID(PRID), getInternalFromPRID(PRID))
    }

    private fun filterTotal(key: String, filter: Any, from: Int, to: Int): Array<PrenotationObject> {
        val res = Vector<PrenotationObject>()
        for (i in from..to) {
            res.addAll(selectResults(key, filter, searchWithDate(i)))
        }
        return res.toTypedArray()
    }

    private fun selectResults(key: String, filter: Any, elements: Array<PrenotationObject>): Vector<PrenotationObject> {
        val res = Vector<PrenotationObject>()
        for (ele in elements) if (ele.getElementByKey(key).toString().contentEquals(filter.toString())) res.add(ele)
        return res
    }

    fun filterResults(key: String, filter: Any, elements: Array<PrenotationObject>): Array<PrenotationObject> {
        return selectResults(key, filter, elements).toTypedArray()
    }

    private fun listerTotal(from: Int, to: Int): Array<PrenotationObject> {
        val res = Vector<PrenotationObject>()
        for (i in from..to) {
            val tmp: Array<PrenotationObject> = searchWithDate(i)
            for (ele in tmp) res.add(ele)
        }
        return res.toTypedArray()
    }

    fun listerTotal(): Array<PrenotationObject> {
        return listerTotal(infoMinYear, infoMaxYear)
    }

    fun filterTotal(key: String, filter: Any): Array<PrenotationObject> {
        return filterTotal(key, filter, infoMinYear, infoMaxYear)
    }

    fun filterFromYear(year: Int, key: String, filter: Any): Array<PrenotationObject> {
        return filterTotal(key, filter, year, infoMaxYear)
    }

    fun filterFromToYear(from: Int, to: Int, key: String, filter: Any): Array<PrenotationObject> {
        return filterTotal(key, filter, from, to)
    }

    fun listerFromYear(year: Int): Array<PrenotationObject> {
        return listerTotal(year, infoMaxYear)
    }

    private fun filterResultsInDate(filteredData: Array<PrenotationObject>, dat: Date, from: Boolean): Array<PrenotationObject?> {
        var d: Date = dat
        val filter = Vector<PrenotationObject>()
        d = clearHMd(d)
        d = getSTDdate(d)
        for (i in filteredData.indices) {
            val ThisP = filteredData[i]
            var thisd: Date = ThisP.date
            thisd = getSTDdate(thisd)
            if ((thisd.after(d) || thisd == d) && from || thisd.before(d) && !from) filter.add(ThisP)
        }
        return filter.toTypedArray()
    }

    fun filterFromDateInResults(d: Date, filtered: Array<PrenotationObject>): Array<PrenotationObject?> {
        return filterResultsInDate(filtered, d, true)
    }

	fun filterFromDate(d: Date, key: String, filter: Any): Array<PrenotationObject?> {
        return filterFromDateInResults(d, filterFromYear(getYear(d), key, filter))
    }

    fun listerFromDate(d: Date): Array<PrenotationObject?> {
        return filterFromDateInResults(d, listerFromYear(getYear(d)))
    }

    fun filterWithDateInResults(year: Int, month: Int, day: Int, filteredData: Array<PrenotationObject>): Array<PrenotationObject> {
        val filter = Vector<PrenotationObject>()
        for (i in filteredData.indices) {
            val ThisP = filteredData[i]
            if (ThisP.year == year && ThisP.month == month && ThisP.day == day) filter.add(ThisP)
        }
        return filter.toTypedArray()
    }

    fun filterWithDateInResults(d: Date, filteredData: Array<PrenotationObject>): Array<PrenotationObject> {
        return filterWithDateInResults(getYear(d), getMonth(d), getDay(d), filteredData)
    }

    fun listerInDate(d: Date): Array<PrenotationObject> {
        val y = getYear(d)
        return filterWithDateInResults(d, listerTotal(y, y))
    }

    fun listPrenotationSorter(Array: Array<PrenotationObject>) {
        Arrays.parallelSort(Array) { o1, o2 ->
            val time = o1.date.time - o2.date.time
            if (time == 0L) 0 else if (time > 0) 1 else -1
        }
    }

    fun getDateFrom(y: Int, M: Int, d: Int, h: Int, m: Int, s: Int): Date {
        val c = Calendar.getInstance()
        c[y, M - 1, d, h, m] = s
        return c.time
    }

    fun getDateFrom(y: Int, M: Int, d: Int): Date {
        return getDateFrom(y, M, d, 0, 0, 0)
    }

    fun clearHMd(d: Date): Date {
        return getDateFrom(getYear(d), getMonth(d), getDay(d), 0, 0, 0)
    }

	fun sumDayDate(d: Date?, day: Int): Date {
        val res = Calendar.getInstance()
        res.time = d
        res.add(Calendar.DATE, day)
        return Date(res.time.time)
    }

	fun fromDateToString(d: Date?): String {
        return SimpleDateFormat("dd-MM-yyyy").format(d)
    }

    fun fromStringToDate(date: String?): Date? {
        return try {
            SimpleDateFormat("dd-MM-yyyy").parse(date)
        } catch (e: ParseException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }

    fun verifyDistance(d: Date, idDon: Int, prenots: Array<PrenotationObject> = filterTotal(IDDON_KEY, idDon) ): Boolean {
        //val datePRT = mutableListOf<Date>()
        val datePRT = Array(prenots.size+1){i -> if (i==prenots.size)  d else prenots[i].date}
       // for (i in prenots.indices) datePRT.add(prenots[i].date)
        //datePRT.add(d)
        Arrays.parallelSort(datePRT) { a, b ->
            val res = a.time - b.time
            if (res > 0) 1 else if (res == 0L) 0 else -1
        }
        for (i in 1 until datePRT.size) {
            if (sumDayDate(datePRT[i - 1], NEXT_DAY_FORM_DONATION).after(datePRT[i])) {
                return false
            }
        }
        return true
    }


	fun getLastDonation(idDon: Int): Date? {
        val donation = Vector<Date>()
        var max = Date(0)
        for (i in 0 until DBManagment.self.noRowsDonation) {
            if (DBManagment.self.getDonationIdDon(i) == idDon) {
                donation.add(DBManagment.self.getDonationDate(i))
            }
        }
        for (i in donation.indices) {
            if (max.before(donation[i])) {
                max = donation[i]
            }
        }
        return if (max.time == 0L) {
            null
        } else max
    }

	fun verifyDistanceLastDonation(d: Date, idDon: Int): Boolean {
        val last = getLastDonation(idDon) ?: return true
        return sumDayDate(last, NEXT_DAY_FORM_DONATION) < d
    }

    fun dataPermitted(d: Date, idDon: Int): Boolean {
        return verifyDistanceLastDonation(d, idDon) && verifyDistance(d, idDon)
    }

    fun dataPermitted(d: Date, idDon: Int, res: Array<PrenotationObject>): Boolean {
        return verifyDistanceLastDonation(d, idDon) && verifyDistance(d, idDon, res)
    }

    fun getAge(birth: Date, inDate: Date): Int {
        val birth_ = getSTDdate(birth)
        val inDate_ = getSTDdate(inDate)
        val age = Date(inDate_.time - birth_.time)
        return getYear(age) - 1970
    }

	fun donatorAgePermitted(birth: Date, inDate: Date): Boolean {
        val age = getAge(birth, inDate)
        return age in MIN_AGE_LIMIT..MAX_AGE_LIMIT
    }

    private fun getFromDate(type: String, data: Date?): Int {
        return try {
            Integer.valueOf(SimpleDateFormat(type).format(data))
        } catch (e: NullPointerException) {
            -1
        }
    }

    fun getYear(d: Date): Int {
        return getFromDate("yyyy", d)
    }

    fun getMonth(d: Date): Int {
        return getFromDate("MM", d)
    }

    fun getDay(d: Date): Int {
        return getFromDate("dd", d)
    }

    fun getHour(d: Date): Int {
        return getFromDate("hh", d)
    }

    fun getMinute(d: Date): Int {
        return getFromDate("mm", d)
    }

    fun getSecond(d: Date): Int {
        return getFromDate("ss", d)
    }

    fun getSTDdate(d: Date): Date {
        val c = Calendar.getInstance()
        c.time = d
        return c.time
    }
}