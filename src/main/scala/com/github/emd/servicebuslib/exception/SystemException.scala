/*
 * Copyright (c) 2017-2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.exception

/**
 * SystemException is thrown when unexpected error occurs during runtime.
 * Depending on the nature of the exception, failed transaction caused by
 * SystemException may be rolled back and retried.
 *
 */
//class SystemException private(ex: RuntimeException) extends RuntimeException(ex) {
//  def this(message: String) = this(new RuntimeException(message))
//  def this(message: String, throwable: Throwable) = this(new RuntimeException(message, throwable))
//}

class SystemException(message: String = null, cause: Throwable = null)
    extends RuntimeException(SystemException.defaultMessage(message, cause), cause)

object SystemException {
  def defaultMessage(message: String, cause: Throwable): String =
    if (message != null) message
    else if (cause != null) cause.toString
    else null
}
