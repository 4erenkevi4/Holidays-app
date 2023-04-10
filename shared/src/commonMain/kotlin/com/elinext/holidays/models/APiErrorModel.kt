package com.elinext.holidays.models

data class ApiErrorModel(
    val responseErrorCode: Int,
    val serverErrorMessage: String,
    val message: String =
        if (serverErrorMessage.contains("Unable to resolve host") || responseErrorCode == 444)
            "To use the Elinext Holidays app, an internet connection is required.\n" +
                    "Please check your connection and try again."
        else
            " error $responseErrorCode \n$serverErrorMessage"
)

