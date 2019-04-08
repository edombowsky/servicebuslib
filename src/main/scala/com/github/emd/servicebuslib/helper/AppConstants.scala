/*
 * Copyright (c) 2018-2019 ABB. All rights reserved.
 */

package com.github.emd.servicebuslib.helper

import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object AppConstants {
  val WINDOW_WIDTH: Int = 800
  val WINDOW_HEIGHT: Int = 600

  val AppDataFileName: String = "./.servicebuslib.json"

  val ApplicationIcon: String = "/images/Azure Service Bus.png"
  val AboutDialogIcon: String = "/images/about_icon.png"

  val MultipleFileListDeliminator: String = ", "

  val NoReceiverLogMessage = "Unable to read messages, there is no receiver"

  val DateFormatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC)

  val LogPath: String = "logs"
  val LogIntroduction: String =
    """
      |
      |+---------------------------+
      || ServiceBusHelper Starting |
      |+---------------------------+
    """.stripMargin
}
