/*
 * Copyright (c) 2018 ABB. All rights reserved.
 *
 */

package com.github.emd.servicebuslib.servicebus

import scala.collection.mutable
import scalafx.beans.property.LongProperty
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.StringProperty

final case class IMessageSummary(
    messageId_ : String,
    sequenceNumber_ : Long,
    label_ : String,
    enqueuedTimeUtc_ : String,
    expiresAtUtc_ : String,
    body_ : Array[Byte],
    customProperties_ : mutable.Map[String, AnyRef],
    properties_ : IMessageProp,
    props_ : Map[String, String]) {

  val messageId = new StringProperty(this, "MessageId", messageId_)
  val sequenceNumber = new LongProperty(this, "SequenceNumber", sequenceNumber_)
  val label = new StringProperty(this, "Label", label_)
  val enqueuedTimeUtc = new StringProperty(this, "EnqueuedTimeUtc", enqueuedTimeUtc_)
  val expiresAtUtc = new StringProperty(this, "EnqueuedTimeUtc", expiresAtUtc_)
  val body = new StringProperty(this, "Body", new String(body_))
  val customProperties = new ObjectProperty[mutable.Map[String, AnyRef]](this, "CustomProperties", customProperties_)
  val properties = new ObjectProperty[IMessageProp](this, "Properties", properties_)
  val correlationId = new StringProperty(this, "CorrelationId", props_("CorrelationId"))

  override def toString: String = {
    s"""
       |MessageId:        $messageId_
       |SequenceNumber:   $sequenceNumber_
       |Label:            $label_
       |EnqueuedTimeUtc:  $enqueuedTimeUtc_
       |ExpiresAtUtc:     $expiresAtUtc_
       |CustomProperties: ${pprint.PPrinter.BlackWhite.apply(customProperties_, 120, 4096).toString()
    }
     """.stripMargin
    }
  }
