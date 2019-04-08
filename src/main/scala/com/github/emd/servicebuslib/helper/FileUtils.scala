/*
 * Copyright (c) 2017-2018 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.helper

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Paths

object FileUtils {

  def fileExists(name: String): Boolean =
    Files.exists(Paths.get(name), LinkOption.NOFOLLOW_LINKS)
}
