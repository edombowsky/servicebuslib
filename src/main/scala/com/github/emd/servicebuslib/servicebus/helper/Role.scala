/*
 * Copyright (c) 2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

/**
 * Types of roles that the corresponding entity entityType would have.
 */
sealed trait Role extends EnumEntry
object Role extends Enum[Role] {

  val values: immutable.IndexedSeq[Role] = findValues

  case object Receiver extends Role
  case object DeadLetterReceiver extends Role
  case object Sender extends Role
  case object Purger extends Role
}
