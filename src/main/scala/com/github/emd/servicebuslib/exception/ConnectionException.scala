/*
 * Copyright (c) 2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.exception

class ConnectionException(message: String = null, cause: Throwable = null) extends
    RuntimeException(ConnectionException.defaultMessage(message, cause), cause)

object ConnectionException {
  def defaultMessage(message: String, cause: Throwable): String =
    if (message != null) message
    else if (cause != null) cause.toString
    else null
}