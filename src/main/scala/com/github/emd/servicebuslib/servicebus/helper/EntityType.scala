/*
 * Copyright (c) 2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

/**
 * Service Bus entity components that are currently available in our system.
 */
sealed trait EntityType extends EnumEntry
object EntityType extends Enum[EntityType] {

  val values: immutable.IndexedSeq[EntityType] = findValues

  case object Queue extends EntityType
  case object Topic extends EntityType
  case object Subscription extends EntityType
  case object DeadLetter extends EntityType
  case object SubscriptionDeadLetter extends EntityType
}
