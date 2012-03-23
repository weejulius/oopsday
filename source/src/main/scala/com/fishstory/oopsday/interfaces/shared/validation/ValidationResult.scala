package com.fishstory.oopsday.interfaces.shared.validation

import collection.mutable.ListBuffer
import util.matching.Regex.Match
import util.matching.Regex


/**
 * Used to place validation result shown to users
 * It is possible to retain the result for several rounds of validation
 */
class ValidationResult {

  var messageValues: List[ListBuffer[Option[Seq[String]]]] = List.empty

  startNewRound()

  /**
   * add the message values which is used to show message to users
   * if failed to validated a expression in the current round
   * @param values
   */
  def +(values: List[String]) {
    messageValues.head += Some(values)
  }

  def noMessageValue() {
    messageValues.head += None
  }

  /**
   * clear up the existing message values in the current validation
   */
  def clearMessageValuesOfCurrentRound() {
    val size = messageValues.head.size
    for (a: Int <- 0 until size - 1) {
      messageValues.head.updated(a, None)
    }
  }

  /**
   * True if there is not failure in the xth validation
   * @param x
   */
  def isSatisfiedAt(x: Int): Boolean = {
    x >= 0 && x < messageValues.size && messageValues(x).forall(_.isEmpty)
  }

  def isSatisfied: Boolean = {
    messageValues.forall(_.forall(_.isEmpty))
  }

  /**
   * True if the expression is satisfied or not executed
   * @param x  the index of rounds to validate
   * @param y  the index of expressions
   */
  def isSatisfiedAt(x: Int, y: Int): Boolean = {
    val roundNum = messageValues.size - x - 1
    roundNum >= 0 && x < messageValues.size && y >= 0 && y < messageValues(x).size && messageValues(roundNum)(y).isEmpty
  }

  /**
   * print out the error message show to user by the message template and values
   * @param x
   * @param y
   * @param messageTemplate
   * @return
   */
  def print(x: Int, y: Int, messageTemplate: String): String = {
    val roundNum = messageValues.size - x - 1
    if (x >= 0 && x < messageValues.size && y >= 0 && y < messageValues(x).size && !messageValues(roundNum)(y).isEmpty) {
      fillValuesOfMessage(messageValues(roundNum)(y).get.toList)(messageTemplate)
    }
    else ""
  }

  def startNewRound() {
    messageValues = ListBuffer.empty[Option[Seq[String]]] :: messageValues
  }

  val _pattern: Regex = """&_([0-9]?)""".r

  /**
   * Replace the placeholder with values in the message template
   * @param messageValue
   * @param messageTemplate
   */
  def fillValuesOfMessage(messageValue: List[String])(messageTemplate: String): String = {
    var indexOfMatch = -1
    _pattern.replaceAllIn(messageTemplate, (m: Match) => {
      val sequence = m.group(1)
      indexOfMatch = indexOfMatch + 1
      if (!sequence.isEmpty) messageValue(sequence.toInt).toString
      else messageValue(indexOfMatch)
    })
  }
}

