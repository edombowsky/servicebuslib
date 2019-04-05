/*
 * Copyright (c) 2018-2019 ABB - All rights reserved
 */

package com.github.emd.servicebuslib.helper

import java.nio.file.Paths

import scribe.Level
import scribe.Logger
import scribe.format.Formatter
import scribe.writer.FileWriter
import scribe.writer.file.LogPath


object FileLogger {
  private lazy val fileWriter: FileWriter =
    FileWriter()
      .maxLogs(5)
      // FIXME:: does this work on windows and/or does it conflict with rolling
      // .maxSize(5000)
      .nio
      .autoFlush
      // FIXME:: Is this really working for windows
      //.path(_ => Paths.get("logs/ServiceBusHelper.log"))
      //.rolling(LogPath.daily("ServiceBusHelper"), checkRate = 0.millis)
      //.path(_ => Paths.get(System.getProperty("user.dir"),
      //                       //"visena",
      //                       "logs",
      //                       //moduleName,
      //                       "servicebuslib.log"))
      //.rolling(LogPath.daily(prefix = "servicebuslib",
      //                       directory = Paths.get(System.getProperty("user.dir"),
      //                                             //"visena",
      //                                             "logs")),
      //         checkRate = 0.millis)
      .path(LogPath.daily(prefix="ServiceBusHelper",
                          directory = Paths.get(System.getProperty("user.dir"), "logs")))

  lazy val fileLogger: Logger =
    Logger.empty.orphan()
      .clearHandlers()
      .withHandler(formatter = Formatter.classic, writer = fileWriter)
      .withMinimumLevel(Level.Debug)
}
