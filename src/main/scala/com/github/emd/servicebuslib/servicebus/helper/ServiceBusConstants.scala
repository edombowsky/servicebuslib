/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import java.util.UUID

object ServiceBusConstants {
  val MaxTransientExceptionAttempts: Int = 10
  val OriginatingApp: String = "EMD-SBH"
  val ApplicationConfigurationFile: String = "application.conf"
  val DefaultPriority: String = "3"
  val DefaultMessageIdPrefix: String = ""
  val NullUUID: UUID = new UUID(0L, 0L)

  val SendMessageLabel: String = "SBH-Message"

  val NoConnection: String = "-" * 15

  val Connected: String = "connected"
  val Disconnected: String = "disconnected"

  val WorkOrderNumberTag: String = "@_WORKORDER_NUMBER_TAG_"
  val OrderNumberTag: String = "@_ORDER_NUMBER_TAG_"
  val MessageIdTag: String = "@_MESSAGE_ID_TAG_"
  val UuidTag: String = "@_UUID_TAG_"
  val DateTag: String = "@_DATE_"
  val DateTimeTag: String = "@_DATE_TIME_TAG_"
  val TimeTag: String = "@_TIME_TAG_"
  val RandomAlphaNumTag: String = "@_RANDOM_ALPHA_TAG_"
  val RandomNumber: String = "@_RANDOM_NUMBER_TAG_"
  val PriorityTag: String = "@_PRIORITY_TAG_"

  val DefaultWorkOrder: String = "EMD00000000001"

  val SubscriptionPathPattern: String = "%s/subscriptions/%s"
  val DeadLetterPathPattern: String = "%s/$DeadLetterQueue"

  val ListenKeyName: String = "Listen"
  val SendKeyName: String = "Send"
}
