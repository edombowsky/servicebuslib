/*
 * Copyright (c) 2018 ABB. All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import java.util.function.UnaryOperator
import javafx.scene.control.TextFormatter.Change

import scalafx.scene.control.TextFormatter

/**
 * Idea from: [[https://uwesander.de/the-textformatter-class-in-javafx-how-to-restrict-user-input-in-a-text-field.html#the-textformatter-class-in-javafx-how-to-restrict-user-input-in-a-text-field]]
 */
object TextFieldUtils {
  /**
   * Return a textFormatter that will limit text to positive integers of a specific length
   *
   * @param length maximum length the positive integer allowed
   *
   * @return
   */
  def getTextFormatter(length: Int): TextFormatter[String] = {
    new TextFormatter[String](getFilter(length))
  }

  /**
   * Return a filter that will limit input to positive integers of a specified length.
   * The approach is to intercept the user input before it's written into the text
   * property and thus fires only one event.
   *
   * @param length maximum length the positive integer allowed
   *
   * @return UnaryOperator to filter input to specified size of positive integers
   */
  private def getFilter(length: Int): UnaryOperator[Change] = change => {
    val text: String = change.getText
    val allText = change.getControlNewText

    if (!change.isContentChange) change
    else if ((text.matches("[0-9]") || text.isEmpty) &&
      allText.length <= length) change
    else null
  }
}
