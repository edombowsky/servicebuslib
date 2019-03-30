/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.adapter

import scala.collection.immutable
import scala.collection.JavaConverters._

import com.microsoft.azure.servicebus.IMessage
import enumeratum.Enum
import enumeratum.EnumEntry

/**
 * Encapsulates a received message and provides useful functionality.
 */
object MessageAdapter {

  val infoTypeAll: scala.collection.mutable.Set[InfoType] =
    scala.collection.mutable.Set(InfoType.IdValues, InfoType.MsgProperties, InfoType.MsgContent)

  implicit class RichToString(val x: java.nio.ByteBuffer) extends AnyVal {
    def byteArrayToString(): String = new String(x.array.takeWhile(_ != 0),"UTF-8")
  }

  /**
   * Used to associate an action to a specific Service Bus message.
   * An action either is what happened to the message / is what needs to be done to the message.
   */
  sealed trait InfoType extends EnumEntry

  object InfoType extends Enum[InfoType] {
    val values: immutable.IndexedSeq[InfoType] = findValues
    case object IdValues extends InfoType
    case object MsgProperties extends InfoType
    case object MsgContent extends InfoType
    case object All extends InfoType
  }

  /**
   * Puts important information about the message in the map. Information such as its
   * Message ID, Correlation ID, all the way to the Message Content itself based on
   * the specified information types.
   *
   * @param message
   * @param properties
   * @param info
   */
  @scala.annotation.tailrec
  def addToProperties(
    message: IMessage,
    properties: scala.collection.mutable.Map[String, String],
    info: scala.collection.mutable.Set[InfoType])
  : Unit = {
    //info: util.EnumSet[InfoType]): Unit = {

    if (!info.contains(InfoType.All)) {

      info.foreach {
        case InfoType.IdValues =>
          properties += ("Message ID" -> Option(message.getMessageId).getOrElse("NULL"))
          properties += ("Session ID" -> Option(message.getSessionId).getOrElse("NULL"))
          properties += ("Correlation ID" -> Option(
            message.getCorrelationId).getOrElse("NULL"))
        case InfoType.MsgProperties =>
          val p = message.getProperties.asScala
          if (p == null) properties += ("Message Properties" -> "NULL")
          else {
            val ppp: String = pprint.PPrinter.BlackWhite.apply(
              message.getProperties.asScala).toString
            properties += ("Message Properties" -> ppp)
          }
        case InfoType.MsgContent =>
          properties += ("Message Content" -> Option(
            new String(message.getBody)).getOrElse("NULL"))
        //case InfoType.All =>
        //  properties += ("Message ID" -> Option(message.getMessageIdTextField).getOrElse("NULL"))
        //  properties += ("Session ID" -> Option(message.getSessionIdTextField).getOrElse("NULL"))
        //  properties += ("Correlation ID" -> Option(message.getCorrelationId).getOrElse("NULL"))
        //  val ppp: String = pprint.PPrinter.BlackWhite.apply(message.getProperties.asScala).toString
        //  properties += ("Message Properties" -> Option(ppp).getOrElse("NULL"))
        //  properties += ("Message Content" -> Option(message.getBody.toString).getOrElse("NULL"))
      }
    } else {
      //addToProperties(message, properties, util.EnumSet.complementOf(info))
      //val complementInfoTypeSet = MessageAdapter.infoTypeAll &~ info
      addToProperties(
        message,
        properties,
        scala.collection.mutable.Set(InfoType.IdValues,
          InfoType.MsgProperties,
          InfoType.MsgContent))
    }
  }

  //def toStr(message: Option[IMessage], level: mutable.Set[InfoType]): _root_.scala.Predef.String = ???

  /**
   * @return important information about the message in a readable format.
   *         Information such as its Message ID, Correlation ID, all the way
   *         to the Message Content itself based on the specified information
   *         types.
   */
  def toStr(
    message: IMessage,
    info: scala.collection.mutable.Set[MessageAdapter.InfoType]): String = {

    val properties: scala.collection.mutable.Map[String, String] =
      new collection.mutable.LinkedHashMap[String, String]()

    addToProperties(message, properties, info)

    pprint.PPrinter.BlackWhite.apply(properties, 120, 4096).toString()
  }
}

class MessageAdapter(val message: Option[IMessage]) {

  var deadLetterReason: String = ""
  var deadLetterErrorDescription: String = ""

  if (message.isEmpty)
    throw new IllegalArgumentException(
      "Message Adapter can't be initialized without a Service Bus message")

  def toString(level: scala.collection.mutable.Set[MessageAdapter.InfoType]): String =
    MessageAdapter.toStr(message.get, level)

  /**
   * @see [[MessageAdapter#toString(IMessage, EnumSet)]]
   */
  override def toString: String =
    toString(scala.collection.mutable.Set(MessageAdapter.InfoType.All))
}
