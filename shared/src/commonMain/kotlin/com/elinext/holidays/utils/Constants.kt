package com.elinext.holidays.utils

import java.util.*

object Constants {
    const val HOLIDAYS_APP = "HOLIDAYS_APP"
    const val OFFICE_ID = "OFFICE_ID"
    const val OFFICE_COUNTRY = "OFFICE_COUNTRY"
    const val NOTIFICATIONS_COUNTRY = "NOTIFICATION_COUNTRY"
    const val NOTIFICATION_SETTINGS = "NOTIFICATION"
    const val NOTIFICATION_DAY = "NOTIFICATION_DAY"
    const val NOTIFICATION_HOUR = "NOTIFICATION_HOUR"
    const val NOTIFICATION_SP_KEY = "NOTIFICATION_SP_KEY"


    const val BASE_URL = "http://holidays.elinext.com/api/"
    const val EMPTY_TYPE_ITEM = 0
    const val FULL_TYPE_ITEM = 1
    const val SMALL_FULL_TYPE_ITEM = 3
    const val HOLIDAY = "HOLIDAY"
    const val WORKING_WEEKEND = "WORKING_WEEKEND"
    val WEEK_STARTS_ON_MONDAY = listOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY
    )
    val WEEK_STARTS_ON_SUNDAY = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
    )

}