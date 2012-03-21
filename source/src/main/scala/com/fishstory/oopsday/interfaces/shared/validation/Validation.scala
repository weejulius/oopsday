package com.fishstory.oopsday.interfaces.shared.validation


/**
 * Used to construct the validation expressions and evaluate it
 */
trait Validation {

  case object isEmpty extends CommonExpression {

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a == null || a.isEmpty, result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isEmpty || a.get.isEmpty, result, a.toString) || evaluate(a.get.head, result)
  }

  case class MaxLength(length: Int) extends CommonExpression {

    def evaluate(a: String, result: ValidationResult): Boolean =
      evaluate1(a.length <= length, result, a, length.toString)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(!a.isEmpty && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)

  }

  case object notBlank extends CommonExpression {

    def evaluate(a: String, result: ValidationResult): Boolean = evaluate1(a != null && !a.isEmpty, result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isDefined && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)
  }

  case object isNumeric extends CommonExpression {

    def evaluate(a: String, result: ValidationResult): Boolean =
      evaluate1(a.length > 0 && a.forall(_.isDigit), result, a)

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean =
      evaluate2(a.isDefined && !a.get.isEmpty, result, a.toString) && evaluate(a.get.head, result)
  }

  abstract class Expression {
    /**
     * Used to encapsulate the logic of validation
     * True if the value passes the evaluation of the expression
     * @param value
     */
    def evaluate[A](value: A, result: ValidationResult): Boolean
  }

  /**
   * the basic of the common used expressions
   */
  abstract class CommonExpression extends Expression {


    protected def evaluate2(a: Boolean, result: ValidationResult, values: String*): Boolean = {
      if (!a) result + (values)
      a
    }

    protected def evaluate1(isSatisfied: Boolean, result: ValidationResult, values: String*): Boolean = {
      if (!isSatisfied) result + (values)
      else result.addEmpty
      isSatisfied
    }

    def evaluate(a: String, result: ValidationResult): Boolean

    def evaluate(a: Option[Seq[String]], result: ValidationResult): Boolean


    def evaluate[A](a: A, result: ValidationResult): Boolean = {
      a match {
        case string: String => evaluate(string, result)
        case option: Option[Seq[String]] => evaluate(option, result)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }
  }

  case class validate[A](var value: A) {

    private var currentExpression: Expression = null
    private var validationResult: ValidationResult = new ValidationResult()
    private var isCurrentExpressionSatisfied = false
    private var hasFailedRound = false

    def using(_expression: Expression) = {
      currentExpression = _expression
      isCurrentExpressionSatisfied = currentExpression.evaluate(value, validationResult)
      this
    }

    def and(newExpression: Expression) = {
      if (isCurrentExpressionSatisfied) {
        using(newExpression)
      }
      this
    }

    def or(newExpression: Expression) = {
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


}