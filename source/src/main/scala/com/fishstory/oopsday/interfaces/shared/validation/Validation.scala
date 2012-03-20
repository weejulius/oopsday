package com.fishstory.oopsday.interfaces.shared.validation

import util.matching.Regex
import util.matching.Regex.Match

/**
 * Used to construct the validation expressions and evaluate it
 */
trait Validation {

  val _pattern: Regex = """<[0-9]?>""".r

  /**
   * Replace the placeholder with values in the message
   * @param value
   * @param message
   */
  def fillValuesOfMessage(value: List[String])(message: String): String = {
    var indexOfMatch = -1
    _pattern.replaceAllIn(message, (m: Match) => {
      val sequence = m.toString.charAt(1)
      indexOfMatch = indexOfMatch + 1
      if (sequence.isDigit) value(sequence.toString.toInt).toString
      else value(indexOfMatch)
    })
  }

  object evaluating {

    private def evaluate[A](_expression: Expression[A], value: A): Boolean = {
      var expression: Expression[A] = _expression.head
      expression.results.registerNewRound()
      var result = false
      while (expression != null) {
        result = expression.evaluate(value)
        if ((result && !expression.and) || (!result && expression.and)) {
          return result
        }
        if (!result && !expression.and) {
          expression.results.clearRound
        }
        expression = expression.next
      }
      result
    }

    def apply[A](_expression: Expression[A], values: A*): Boolean = {
      var isEverViolated = false
      for (value <- values) {
        if (evaluate(_expression, value)) isEverViolated = true
      }
      isEverViolated
    }
  }

  case class IsEmpty[A]() extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a == null || a.isEmpty, a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isEmpty || a.get.isEmpty, a.toString) || evaluate(a.get.head)
  }

  case class MaxLength[A](length: Int) extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a.length <= length, a, length.toString)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(!a.isEmpty && !a.get.isEmpty, a.toString) && evaluate(a.get.head)

  }

  case class NotBlank[A]() extends Expression[A] {
    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a != null && !a.isEmpty, a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isDefined && !a.get.isEmpty, a.toString) && evaluate(a.get.head)
  }

  case class IsNumeric[A]() extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a.length > 0 && a.forall(_.isDigit), a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isDefined && !a.get.isEmpty, a.toString) && evaluate(a.get.head)
  }

  /**
   * Used to construct the expressions and chain the expressions to be validated,
   * and the results is put in the head nextExpression
   */
  abstract class Expression[A] {

    var and = true
    var next: Expression[A] = null
    var head: Expression[A] = this
    private var validationResult: ValidationResult = null

    def results: ValidationResult = {
      if (head == this && validationResult == null) {
        validationResult = new ValidationResult()
      }
      head.validationResult
    }

    /**
     * Validation is passed either of the both expressions is passed
     * @param nextExpression
     * @return
     */
    def ||(nextExpression: Expression[A]): Expression[A] = {
      and = false
      next = nextExpression
      nextExpression.head = this.head
      nextExpression
    }

    /**
     * validation is passed both the expressions are passed
     * @param nextExpression
     * @return
     */
    def &&(nextExpression: Expression[A]): Expression[A] = {
      next = nextExpression
      nextExpression.head = this.head
      nextExpression
    }

    /**
     * true if it has the next expression
     */
    def hasNext = head == this || next != null

    /**
     * Used to express the logic of validation
     * True if the value passes the evaluation of the expression
     * @param value
     */
    def evaluate(value: A): Boolean

    def retry(b: Expression[A]): Expression[A] = {
      b.head.validationResult = head.validationResult
      head = b.head
      b.head
    }

    protected def evaluate2(a: Boolean, values: String*): Boolean = {
      if (!a) results + (Some(fillValuesOfMessage(values.toList)))
      a
    }

    protected def evaluate1(a: Boolean, values: String*): Boolean = {
      val result = a
      var message: Option[(String) => String] = None
      if (!result) message = Some(fillValuesOfMessage(values.toList))
      results + (message)
      result
    }
  }

  case class ensure[A](var value: A) {

    private var expressions: Map[Expression[A], A] = Map.empty
    private var currentExpression: Expression[A] = null
    private var result: ValidationResult = new ValidationResult()

    def that(_expression: Expression[A]) = {
      result + _expression.evaluate(value)
      currentExpression = _expression
      expressions += (currentExpression -> value)
      this
    }

    def and(newExpression: Expression[A]) = {
      currentExpression && newExpression
      this
    }

    def or(newExpression: Expression[A]) = {
      currentExpression || newExpression
      this
    }

    def ensure(b: A) = {
      value = b
      this
    }

    def isSatisfied = {
      var isEverFailed = false
      for (expression <- expressions) {
        if (!evaluating(expression._1, expression._2)) {
          isEverFailed = true
        }
      }
      !isEverFailed
    }
  }


  case class Evaluates[A](var a: A) {

    private var expressions: Map[A, Expression[A]] = Map.empty
    private var currentExpression: Expression[A] = null

    def using(_expression: Expression[A]) = {
      currentExpression = _expression
      expressions += (a -> currentExpression)
      this
    }

    def and(newExpression: Expression[A]) = {
      currentExpression && newExpression
      this
    }

    def or(newExpression: Expression[A]) = {
      currentExpression || newExpression
      this
    }

    def another(b: A) = {
      a = b
      this
    }

    def isPassed = {
      var isEverFailed = false
      for (expression <- expressions) {
        if (!evaluating(expression._2, expression._1)) {
          isEverFailed = true
        }
      }
      !isEverFailed
    }
  }


}