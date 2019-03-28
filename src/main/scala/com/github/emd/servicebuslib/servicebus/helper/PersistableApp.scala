/*
 * Copyright (c) 2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import java.nio.file.Files
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardOpenOption

import scala.util.Try

import upickle.default.{ReadWriter => RW}
import upickle.default.macroRW
import upickle.default.read
import upickle.default.write

import com.github.emd.servicebuslib.helper.AppConstants

trait PersistableApp {

  implicit def rw: RW[AppData] = macroRW

  val filePath: Path = FileSystems.getDefault.getPath(AppConstants.AppDataFileName)

  def readAppData(filename: String): Try[AppData] = {
    Try {
      if (!Files.exists(filePath)) AppData()
      else {
        val source = scala.io.Source.fromFile(filename)
        val lines = try source.mkString finally source.close()
        read[AppData](lines)
      }
    }
  }

  def writeAppData(appData: AppData): Try[Path] = {
    Try {
      java.nio.file.Files.write(
        filePath,
        write(appData, 3).getBytes("utf-8"),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)
    }
  }
}
