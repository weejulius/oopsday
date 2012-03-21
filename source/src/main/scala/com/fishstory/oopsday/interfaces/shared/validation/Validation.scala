package com.fishstory.oopsday.interfaces.shared.validation


/**
 * Used to construct the validation expressions and evaluate it
 */
trait Validation {


  object evaluating {

    private def evaluate[A](_expression: Expression[A], value: A): Boolean = {
      var expression: Expression[A] = _expression.head
      //expression.results.startNewRound()
      var isSatisfied = false
      while (expression != null) {
        //isSatisfied = expression.evaluate(value)
        if ((isSatisfied && !expression.and) || (!isSatisfied && expression.and)) {
          return isSatisfied
        }
        if (!isSatisfied && !expression.and) {
          // expression.results.clearRound
        }
        expression = expression.next
      }
      isSatisfied
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

    def evaluate(a: A, result: ValidationResult): Boolean = {
      a match {
        case string: String => evaluate(string, result)
        case option: Option[Seq[String]] => evaluate(option, result)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a == null || a.isEmpty, result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isEmpty || a.get.isEmpty, result, a.toString) || evaluate(a.get.head, result)
  }

  case class MaxLength[A](length: Int) extends Expression[A] {


    def evaluate(a: A, result: ValidationResult): Boolean = {
      a match {
        case string: String => evaluate(string, result)
        case option: Option[Seq[String]] => evaluate(option, result)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a.length <= length, result, a, length.toString)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(!a.isEmpty && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)

  }

  case class NotBlank[A]() extends Expression[A] {
    def evaluate(a: A, result: ValidationResult): Boolean = {
      a match {
        case string: String => evaluate(string, result)
        case option: Option[Seq[String]] => evaluate(option, result)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a != null && !a.isEmpty, result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isDefined && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)
  }

  case class IsNumeric[A]() extends Expression[A] {

    def evaluate(a: A, result: ValidationResult): Boolean = {
      a match {
        case string: String => evaluate(string, result)
        case option: Option[Seq[String]] => evaluate(option, result)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a.length > 0 && a.forall(_.isDigit), result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isDefined && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)
  }

  /**
   * Used to construct the expressions and chain the expressions to be validated,
   * and the results is put in the head nextExpression
   */
  abstract class Expression[A] {

    var and = true
    var next: Expression[A] = null
    var head: Expression[A] = this
    //private var validationResult: ValidationResult = null

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
    def evaluate(value: A, result: ValidationResult): Boolean

    def retry(b: Expression[A]): Expression[A] = {
      head = b.head
      b.head
    }

    protected def evaluate2(a: Boolean, result: ValidationResult, values: String*): Boolean = {
      if (!a) result + (values)
      a
    }

    protected def evaluate1(isSatisfied: Boolean, result: ValidationResult, values: String*): Boolean = {
      if (!isSatisfied) result + (values)
      else result.addEmpty
      isSatisfied
    }
  }

  case class validate[A](var value: A) {

    private var currentExpression: Expression[A] = null
    private var validationResult: ValidationResult = new ValidationResult()
    private var isCurrentExpressionSatisfied = false
    private var hasFailedRound = false

    def using(_expression: Expression[A]) = {
      currentExpression = _expression
      isCurrentExpressionSatisfied = currentExpression.evaluate(value, validationResult)
      this
    }

    def and(newExpression: Expression[A]) = {
      if (isCurrentExpressionSatisfied) {
        using(newExpression)
      }
      this
    }

    def or(newExpression: Expression[A]) = {
      if (!isCurrentExpressionSatisfied) {
        validationResult.clearRound
        using(newExpression)
      }
      this
    }

    def andValidate(b: A) = {
      if (!isCurrentExpressionSatisfied) hasFailedRound = true
      value = b
      validationResult.startNewRound()
      this
    }

    def isSatisfied = {
      !hasFailedRound && isCurrentExpressionSatisfied
    }

    def result(): ValidationResult = validationResult
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