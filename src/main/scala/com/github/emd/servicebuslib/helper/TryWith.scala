/*
 * Copyright (c) 2017 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.helper

import java.io.Closeable
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

/**
 * Scala TryWith that closes resources automatically.
 *
 * source: https://codereview.stackexchange.com/questions/79267/scala-trywith-that-closes-resources-automatically
 */
object TryWith {
  def apply[C <: Closeable, R](resource: => C)(f: C => R): Try[R] =
    Try(resource).flatMap(resourceInstance => {
      try {
        val returnValue = f(resourceInstance)
        Try(resourceInstance.close()).map(_ => returnValue)
      }
      catch {
        case NonFatal(exceptionInFunction) =>
          try {
            resourceInstance.close()
            Failure(exceptionInFunction)
          }
          catch {
            case NonFatal(exceptionInClose) =>
              exceptionInFunction.addSuppressed(exceptionInClose)
              Failure(exceptionInFunction)
          }
      }
    })
}
