package com.jmm.network.interceptors

import okhttp3.logging.HttpLoggingInterceptor


val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
