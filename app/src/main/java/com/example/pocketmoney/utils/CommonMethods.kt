package com.example.pocketmoney.utils

import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.utils.myEnums.DateTimeEnum
import com.example.pocketmoney.utils.myEnums.FilterEnum
import com.example.pocketmoney.utils.myEnums.PaymentHistoryFilterEnum
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private val SDF_YMD_WITH_DASH = SimpleDateFormat("yyyy-MM-dd", Locale.US)
val SDF_d_M_y = SimpleDateFormat("dd MMM yyyy", Locale.US)
fun convertISOTimeToDateTime(isoTime: String): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(isoTime)
        formattedDate = SimpleDateFormat(" dd MMM yyyy").format(convertedDate)
//        formattedDate = SimpleDateFormat("MMM dd,yyyy | HH:mm").format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}

fun convertISOTimeToDate(isoTime: String): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(isoTime)
        formattedDate = SimpleDateFormat("MMM dd,yyyy").format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}

fun convertISOTimeToAny(isoTime: String, myFormatter: SimpleDateFormat): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(isoTime)
        formattedDate = myFormatter.format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}


fun getDateRange(type: DateTimeEnum): String {
    return when (type) {
        DateTimeEnum.LAST_WEEK ->
            getDaysAgo(7)
        DateTimeEnum.LAST_MONTH ->
            getDaysAgo(30)
        DateTimeEnum.LAST_3_MONTH ->
            getDaysAgo(90)
        DateTimeEnum.LAST_6_MONTH ->
            getDaysAgo(180)

        else -> getDaysAgo(0)
    }

}



fun getDateRange(type: FilterEnum):String{
    return when(type){

        FilterEnum.LAST_MONTH ->
            getDayAgo(-30)
        FilterEnum.LAST_WEEK ->
            getDayAgo(-7)
        FilterEnum.YESTERDAY->
            getDayAgo(-1)
        FilterEnum.TODAY->
            getDayAgo(0)
        FilterEnum.TOMORROW->
            getDayAgo(1)
        FilterEnum.THIS_WEEK ->
            getDayAgo(7)
        FilterEnum.THIS_MONTH ->
            getDayAgo(30)

        else-> getDayAgo(0)
    }

}

fun getDateLabelAcToFilter(type: FilterEnum):String{
    return when(type){

        FilterEnum.LAST_MONTH ->
            "Last Month"
        FilterEnum.LAST_WEEK ->
            "Last Week"
        FilterEnum.YESTERDAY->
            "Yesterday"
        FilterEnum.TODAY->
            "Today"
        FilterEnum.TOMORROW->
            "Tomorrow"
        FilterEnum.THIS_WEEK ->
            "This Week"
        FilterEnum.THIS_MONTH ->
            "This Month"

        else-> ""
    }

}

fun getDateRange(type: PaymentHistoryFilterEnum): String {
    return when (type) {
        PaymentHistoryFilterEnum.LAST_WEEK ->
            getDaysAgo(7)
        PaymentHistoryFilterEnum.LAST_MONTH ->
            getDaysAgo(30)
        PaymentHistoryFilterEnum.LAST_3_MONTH ->
            getDaysAgo(90)
        PaymentHistoryFilterEnum.LAST_6_MONTH ->
            getDaysAgo(180)

        else -> getDaysAgo(0)
    }

}

fun IntToOrdinal(i: Int): String {
    val j = i % 10
    val k = i % 100
    if (j == 1 && k != 11) {
        return i.toString() + "st"
    }
    if (j == 2 && k != 12) {
        return i.toString() + "nd"
    }
    return if (j == 3 && k != 13) {
        i.toString() + "rd"
    } else i.toString() + "th"
}

fun getTodayDate(): String {
    return getDaysAgo(0)
}


fun getDaysAgo(daysAgo: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
    return SDF_YMD_WITH_DASH.format(calendar.time)
}
fun getDayAgo(daysAgo: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, daysAgo)
    return SDF_YMD_WITH_DASH.format(calendar.time)
}

fun convertMillisecondsToDate(milliSeconds: Long?, dateFormat: String?): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    if (milliSeconds != null) {
        calendar.timeInMillis = milliSeconds
    }
    return formatter.format(calendar.time)
}

fun getTimeFilter(): List<UniversalFilterItemModel> {
    val filterList = mutableListOf<UniversalFilterItemModel>()

    filterList.add(UniversalFilterItemModel(ID = DateTimeEnum.LAST_WEEK, displayText = "Last Week"))
    filterList.add(
        UniversalFilterItemModel(
            ID = DateTimeEnum.LAST_MONTH,
            displayText = "Last Month"
        )
    )
    filterList.add(
        UniversalFilterItemModel(
            ID = DateTimeEnum.LAST_3_MONTH,
            displayText = "Last 3 Months"
        )
    )
    filterList.add(
        UniversalFilterItemModel(
            ID = DateTimeEnum.LAST_6_MONTH,
            displayText = "Last 6 Months"
        )
    )
    filterList.add(UniversalFilterItemModel(ID = DateTimeEnum.CUSTOM, displayText = "Custom"))
    return filterList
}


fun getTimeFilters(): List<UniversalFilterItemModel> {
    val filterList = mutableListOf<UniversalFilterItemModel>()

    filterList.add(UniversalFilterItemModel(ID = FilterEnum.LAST_WEEK, displayText = "Last Week"))
    filterList.add(
        UniversalFilterItemModel(
            ID = FilterEnum.LAST_MONTH,
            displayText = "Last Month"
        )
    )
    filterList.add(
        UniversalFilterItemModel(
            ID = FilterEnum.LAST_3_MONTH,
            displayText = "Last 3 Months"
        )
    )
    filterList.add(
        UniversalFilterItemModel(
            ID = FilterEnum.LAST_6_MONTH,
            displayText = "Last 6 Months"
        )
    )
    filterList.add(UniversalFilterItemModel(ID = FilterEnum.CUSTOM, displayText = "Custom"))
    return filterList
}


fun getMobileOperatorLogo(id: String): Int {

    return when (id) {
        "Jio" -> R.drawable.ic_jio
        "Airtel" -> R.drawable.ic_airtel
        "Idea", "Vodafone" -> R.drawable.ic_vi_vodafone_idea

        "BSNL" -> R.drawable.ic_bsnl
        "MTNL" -> R.drawable.ic_mtnl
        else -> R.drawable.temp_logo
    }

}


fun TextView.setAmount(amount: Any) {
    text = "₹ $amount"
}

fun RecyclerView.setup(adapter : Adapter){

}