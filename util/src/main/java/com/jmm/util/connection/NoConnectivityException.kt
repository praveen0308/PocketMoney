package com.jmm.util.connection

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No internet!!!"
}