/*
 * Copyright (c) 2018 ABB. All rights reserved.
 *
 */

package com.github.emd.servicebuslib.exception

class UninitializedError(message: String = "uninitialised value", cause: Throwable = null) extends
    RuntimeException(UninitializedError.defaultMessage(message, cause), cause) {

  def this(cause: Throwable) {
    this(Option(cause).map(_.toString).orNull, cause)
  }

  def this() {
    this(null: String)
  }

  def unapply(e: UninitializedError): Option[(String,Throwable)] = Some((e.getMessage, e.getCause))

}

object UninitializedError {
  def defaultMessage(message: String, cause: Throwable): String =
    if (message != null) message
    else if (cause != null) cause.toString
    else null
}
