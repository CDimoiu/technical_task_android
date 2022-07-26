package com.cdimoiu.sliide.models

data class Result<out T>(val status: ResultStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T, message: String? = null): Result<T> =
            Result(status = ResultStatus.SUCCESS, data = data, message = message)

        fun <T> error(data: T?, message: String? = null): Result<T> =
            Result(status = ResultStatus.ERROR, data = data, message = message)

        fun <T> loading(data: T?, message: String? = null): Result<T> =
            Result(status = ResultStatus.LOADING, data = data, message = message)
    }
}