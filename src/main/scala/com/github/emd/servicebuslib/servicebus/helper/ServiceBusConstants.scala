/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import java.util.UUID

object ServiceBusConstants {
  val MaxTransientExceptionAttempts: Int = 10

  val NullUUID: UUID = new UUID(0L, 0L)

  val SubscriptionPathPattern: String = "%s/subscriptions/%s"
  val DeadLetterPathPattern: String = "%s/$DeadLetterQueue"
}
