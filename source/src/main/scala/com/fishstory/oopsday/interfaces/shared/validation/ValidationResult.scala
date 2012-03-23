package com.fishstory.oopsday.interfaces.shared.validation

import collection.mutable.ListBuffer
import util.matching.Regex.Match
import util.matching.Regex


/**
 * Used to place validation result shown to users
 * It is possible to retain the result for several rounds of validation
 */
class ValidationResult {

  var messages: List[ListBuffer[Option[Seq[String]]]] = List.empty

  startNewRound

  /**
   * add the values which is used to show message to users
   * after validating a expression in the current round validation
   * @param values
   */
  def +(values: List[String]) {
    messages.head += Some(values)
  }

  def addEmpty {
    messages.head += None
  }

  /**
   * clear up the failures in the current validation
   */
  def clearRound {
    val size = messages.head.size
    for (a: Int <- 0 until size - 1) {
      messages.head.updated(a, None)
    }
  }

  /**
   * True if there is not failure in the xth validation
   * @param x
   */
  def isSatisfied(x: Int): Boolean = {
    x >= 0 && x < messages.size && messages(x).forall(_.isEmpty)
  }

  def isSatisfied: Boolean = {
    messages.forall(_.forall(_.isEmpty))
  }

  /**
   * True if the expression is satisfied or not executed
   * @param x  the index of rounds to validate
   * @param y  the index of expressions
   */
  def isSatisfiedAt(x: Int, y: Int): Boolean = {
    val roundNum = messages.size - x - 1
    roundNum >= 0 && x < messages.size && y >= 0 && y < messages(x).size && messages(roundNum)(y).isEmpty
  }

  /**
   * print out the error message show to user by the message template and values
   * @param x
   * @param y
   * @param messageTemplate
   * @return
   */
  def print(x: Int, y: Int, messageTemplate: String): String = {
    val roundNum = messages.size - x - 1
    if (x >= 0 && x < messages.size && y >= 0 && y < messages(x).size && !messages(roundNum)(y).isEmpty) {
      fillValuesOfMessage(messages(roundNum)(y).get.toList)(messageTemplate)
    }
    else ""
  }

  def startNewRound() {
    messages = ListBuffer.empty[Option[Seq[String]]] :: messages
  }

  val _pattern: Regex = """&_([0-9]?)""".r

  /**
   * Replace the placeholder with values in the message
   * @param value
   * @param message
   */
  def fillValuesOfMessage(value: List[String])(message: String): String = {
    var indexOfMatch = -1
    _pattern.replaceAllIn(message, (m: Match) => {
      val sequence = m.group(1)
      indexOfMatch = indexOfMatch + 1
      if (!sequence.isEmpty) value(sequence.toInt).toString
      else value(indexOfMatch)
    })
  }


}

