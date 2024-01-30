package com.weichieh.currencyconverter.utils

/**
 * Resource is a generic class that holds a status, data and message. It's used for wrapping responses from repository
 * to provide more detailed information about the status of the operation.
 *
 * @param T the type of the data that is being returned
 * @property status the status of the operation
 * @property data the data that is returned from the operation
 * @property message a message describing the status of the operation
 * @constructor Creates a new Resource instance with the specified status, data and
 * message
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

/**
 * Status enum class represents the state of the operation.
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}