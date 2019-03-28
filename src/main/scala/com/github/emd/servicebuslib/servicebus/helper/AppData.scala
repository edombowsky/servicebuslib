/*
 * Copyright (c) 2018-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import com.github.emd.servicebuslib.helper.AppConstants

final case class AppData(
  originator: Option[String] = None,
  messageId: Option[String] = None,
  sessionId: Option[String] = None,
  priority: Option[String] = Some("3"),
  workOrderNumber: Option[String] = None,
  orderNumber: Option[String] = None,
  sendBatch: Option[Boolean] = Some(false),
  messageDelay: Option[Int] = Some(1),
  numberToSend: Option[String] = None,
  //multipleFileSelection: Option[Boolean] = Some(false),
  messageCount: Option[String] = None,
  messageFiles: Option[Array[String]] = None) {

  override def toString: String =
    s"""
       |Originator       : ${originator.getOrElse("")}
       |Message ID       : ${messageId.getOrElse("")}
       |Session ID       : ${sessionId.getOrElse("")}
       |Priority         : ${priority.getOrElse("")}
       |WorkOrder Number : ${workOrderNumber.getOrElse("")}
       |Order Number     : ${orderNumber.getOrElse("")}
       |Send Batch       : ${sendBatch.getOrElse(false)}
       |Message Delay    : ${messageDelay.getOrElse("")}
       |Number to Send   : ${numberToSend.getOrElse("")}
       |Message Files    : ${messageFiles.getOrElse(Array[String]())
        .mkString(AppConstants.MultipleFileListDeliminator)
    }
    """.stripMargin
}
