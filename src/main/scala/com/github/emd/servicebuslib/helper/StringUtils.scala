/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.helper

import java.time.Instant

object StringUtils {

  /**
   * Confirm if a string is empty. That is, null or empty string.
   *
   * @param x string to be checked
   *
   * @return true if string is empty, false otherwise
   */
  def isEmpty(x: String): Boolean = x == null || x.trim.isEmpty

  /**
   * Generates a Message Id for sending messages.
   *
   * @param messageId the base of the message
   *
   * @return the messageId parameter appended with current timestamp
   */
  def generateMessageId(messageId: Option[String]): String = {
    val nowString = Instant.now.toString
    messageId match {
      case None => nowString
      case Some(str) => if (isEmpty(str)) nowString else s"$str-$nowString"
    }
  }

  /**
   * Pretty prints a Scala messages similar to its source representation.
   * Particularly useful for case classes.
   *
   * @param a                The messages to pretty print
   * @param indentSize       Number of spaces for each indent
   * @param maxElementWidth  Largest element size before wrapping
   * @param depth            Initial depth to pretty print indents
   *
   * @return String representation of a Scala object
   *
   * @see [[https://gist.github.com/carymrobbins/7b8ed52cd6ea186dbdf8]]
   */
  def prettyPrint(a: Any, indentSize: Int = 2, maxElementWidth: Int = 30, depth: Int = 0): String = {

    val indent = " " * depth * indentSize
    val fieldIndent = indent + (" " * indentSize)
    val thisDepth = prettyPrint(_: Any, indentSize, maxElementWidth, depth)
    val nextDepth = prettyPrint(_: Any, indentSize, maxElementWidth, depth + 1)

    a match {
      // Make Strings look similar to their literal form.
      case s: String =>
        val replaceMap = Seq(
          "\n" -> "\\n",
          "\r" -> "\\r",
          "\t" -> "\\t",
          "\"" -> "\\\""
        )
        '"' + replaceMap.foldLeft(s) { case (acc, (c, r)) => acc.replace(c, r) } + '"'

      // For an empty Seq just use its normal String representation.
      case xs: Seq[_] if xs.isEmpty => xs.toString()

      case xs: Seq[_] =>
        // If the Seq is not too long, pretty print on one line.
        val resultOneLine = xs.map(nextDepth).toString()
        if (resultOneLine.length <= maxElementWidth) return resultOneLine
        // Otherwise, build it with newlines and proper field indents.
        val result = xs.map(x => s"\n$fieldIndent${nextDepth(x)}").toString()
        result.substring(0, result.length - 1) + "\n" + indent + ")"

      // Product should cover case classes.
      case p: Product =>
        val prefix = p.productPrefix
        // We'll use reflection to get the constructor arg names and values.
        val cls = p.getClass
        val fields = cls.getDeclaredFields.filterNot(_.isSynthetic).map(_.getName)
        val values = p.productIterator.toSeq
        // If we weren't able to match up fields/values, fall back to toString.
        if (fields.length != values.length) return p.toString
        fields.zip(values).toList match {
          // If there are no fields, just use the normal String representation.
          case Nil => p.toString
          // If there is just one field, let's just print it as a wrapper.
          case (_, value) :: Nil => s"$prefix(${thisDepth(value)})"
          // If there is more than one field, build up the field names and values.
          case kvps =>
            val prettyFields = kvps.map { case (k, v) => s"$fieldIndent$k = ${nextDepth(v)}" }
            // If the result is not too long, pretty print on one line.
            val resultOneLine = s"$prefix(${prettyFields.mkString(", ")})"
            if (resultOneLine.length <= maxElementWidth) return resultOneLine
            // Otherwise, build it with newlines and proper field indents.
            s"$prefix(\n${prettyFields.mkString(",\n")}\n$indent)"
        }

      // If we haven't specialized this type, just use its toString.
      case _ => a.toString
    }
  }
}
