package com.sampurna.pocketmoney.utils.connection

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No internet!!!"
}