package data

import java.util.*

class DBManagment private constructor(private var donatori: AccessDB, private var donazioni: AccessDB){

    companion object {
        private var me : DBManagment? = null
        fun load(a: AccessDB, b: AccessDB) : DBManagment {
            if(me == null){
                me = DBManagment(a,b)
            }
            return me as DBManagment
        }
        val self: DBManagment
        get(){
            if(me == null){
                throw IllegalAccessError()
            }
            return me as DBManagment
        }
        fun loaded(): Boolean{
            return me!=null
        }
        fun unload(){
            me = null
        }
    }
    object DONATOR{
        val TYPE = 0
        val SECTION = 1
        val DATA_SUB = 2
        val BLOOD_GROUP = 3
        val BLOOD_RH = 4
        val ID_DON = 5
        val SURENAME = 6
        val NAME = 7
        val BORN_PLACE = 8
        val BIRTH_DATE = 9
        val LIVE_PLACE = 10
        val LIVE_PLACE_CAP = 11
        val LIVE_PLACE_STREET = 12
        val PHONE = 13
        val RELATION = 14
        val CARD = 15
        val NOTES = 16
    }
    object DONATION{
        val ID_DON = 0
        val DATA = 1
        val CENTER = 2
        val TYPE = 3
    }

    fun getDonatorInfo(idDon: Int): List<Any?> {
        return donatori.getRow(idDon - 1)
    }

    fun getDonationInfo(row: Int): List<Any?> {
        return donazioni.getRow(row)
    }

    private fun getDonatorInformation(idDon: Int, info_id: Int): Any? {
        return getDonatorInfo(idDon)[info_id]
    }

    private fun getDonationInformation(row: Int, info_id: Int): Any? {
        return getDonationInfo(row)[info_id]
    }

    fun getType(idDon: Int): String {
        return getDonatorInformation(idDon, DONATION.TYPE) as String
    }

    fun getLiveCity(idDon: Int): String {
        return getDonatorInformation(idDon, DONATOR.LIVE_PLACE) as String
    }

    fun getLiveStreet(idDon: Int): String {
        return getDonatorInformation(idDon, DONATOR.LIVE_PLACE_STREET) as String
    }

    fun getPhone(idDon: Int): String {
        return getDonatorInformation(idDon, DONATOR.PHONE) as String
    }

    fun getBirthDate(idDon: Int): Date {
        return getDonatorInformation(idDon, DONATOR.BIRTH_DATE) as Date
    }

    fun getName(idDon: Int): String {
        return getDonatorInformation(idDon, DONATOR.NAME) as String
    }

    fun getSurename(idDon: Int): String {
        return getDonatorInformation(idDon, DONATOR.SURENAME) as String
    }

    fun getDonationIdDon(row: Int): Int {
        return (getDonationInformation(row, DONATION.ID_DON) as Double).toInt()
    }

    fun getDonationDate(row: Int): Date {
        return getDonationInformation(row, DONATION.DATA) as Date
    }

    val noRowsDonator: Int
        get() = donatori.noRows
    val noRowsDonation: Int
        get() = donazioni.noRows
}