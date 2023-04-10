package com.elinext.holidays.models

data class ApiErrorModel(
    private val _rawMessage: String? = null,
    val responseErrorCode: Int,
    val serverErrorMessage: String,
    val message: String =
        if (serverErrorMessage.contains("Unable to resolve host"))
            "To use the Elinext Holidays app, an internet connection is required.\n" +
                    "Please check your connection and try again."
        else
            " error $responseErrorCode \n$serverErrorMessage"
)

