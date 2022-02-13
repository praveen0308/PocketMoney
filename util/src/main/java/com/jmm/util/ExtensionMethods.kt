package com.jmm.util

import com.jmm.util.connection.NoConnectivityException

fun Throwable.identify(): String {
    return if (this is NoConnectivityException) this.message
    else "Something went wrong!!!"
}
