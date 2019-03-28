/*
 * Copyright (c) 2018 ABB. All rights reserved.
 *
 */

package com.github.emd.servicebuslib.servicebus

import scalafx.beans.property.LongProperty
import scalafx.beans.property.StringProperty

final case class IMessageProp(
    contentType_ : String,
    correlationId_ : String,
    deliveryCount_ : Long,
    lockedUntilUtc_ : String = "Operation is not valid due to the current state of the object.",
    lockToken_ : String = "Operation is not valid due to the current state of the object.",
    partitionKey_ : String,
    replyTo_ : String,
    replyToSessionId_ : String,
    scheduledEnqueueTimeUtc_ : String,
    sessionId_ : String,
    timeToLive_ : String,
    to_ : String) {

  val contentType = new StringProperty(this, "ContentType", contentType_)
  val correlationId = new StringProperty(this, "CorrelationId", correlationId_)
  val deliveryCount = new LongProperty(this, "DeliveryCount", deliveryCount_)
  val lockedUntilUtc = new StringProperty(this, "lockedUntilUtc", lockedUntilUtc_)
  val lockToken = new StringProperty(this, "LockToken", lockToken_)
  val partitionKey = new StringProperty(this, "PartitionKey", partitionKey_)
  val replyTo = new StringProperty(this, "ReplyTo", replyTo_)
  val replyToSessionId = new StringProperty(this, "ReplyToSessionId", replyToSessionId_)
  val scheduledEnqueueTimeUtc = new StringProperty(this, "ScheduledEnqueueTimeUtc_", scheduledEnqueueTimeUtc_)
  val sessionId = new StringProperty(this, "SessionId", sessionId_)
  val timeToLive = new StringProperty(this, "TimeToLive", timeToLive_)
  val to = new StringProperty(this, "To", to_)

  override def toString: String = {
    s"""
       |ContentType:             $contentType_
       |CorrelationId:           $correlationId_
       |DeliveryCount:           $deliveryCount_
       |LockedUntilUtc:          $lockedUntilUtc_
       |LockToken:               $lockToken_
       |PartitionKey:            $partitionKey_
       |ReplyTo:                 $replyTo_
       |ReplyToSessionId:        $replyToSessionId_
       |ScheduledEnqueueTimeUtc: $scheduledEnqueueTimeUtc_
       |SessionId:               $sessionId_
       |TimeToLive:              $timeToLive_
       |To:                      $to_
     """.stripMargin
  }
}
