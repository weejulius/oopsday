package com.fishstory.oopsday.interfaces.shared.validation


/**
 * Used to construct the validation expressions and evaluateOption it
 */
trait Validation {

  implicit def validate2Boolean[A](v: validate[A]): Boolean = v.isSatisfied

  case object isEmpty extends CommonExpression {

    def evaluateString(a: String, result: ValidationResult): Boolean = evaluate1(a == null || a.isEmpty, result, a)

    override def evaluateOption[A](a: Option[A], result: ValidationResult): Boolean =
      evaluate2(a.isEmpty, result, a.toString) || evaluate(a.get, result)

    override def evaluateSeq[A](a: Seq[A], result: ValidationResult): Boolean =
      evaluate2(a.isEmpty, result, a.toString) || evaluate(a.head, result)

  }

  case class MaxLength(length: Int) extends CommonExpression {

    def evaluateString(a: String, result: ValidationResult): Boolean =
      evaluate1(a.length <= length, result, a, length.toString)
  }

  case object notBlank extends CommonExpression {

    def evaluateString(a: String, result: ValidationResult): Boolean = evaluate1(a != null && !a.isEmpty, result, a)
  }

  case object isNumeric extends CommonExpression {

    def evaluateString(a: String, result: ValidationResult): Boolean =
      evaluate1(a.length > 0 && a.forall(_.isDigit), result, a)
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

    def evaluateString(a: String, result: ValidationResult): Boolean

    def evaluateOption[A](a: Option[A], result: ValidationResult): Boolean = evaluate2(a.isDefined, result, a.toString) && evaluate(a.get, result)

    def evaluateSeq[A](a: Seq[A], result: ValidationResult): Boolean = evaluate2(!a.isEmpty, result, a.toString) && evaluate(a.head, result)

    def evaluate[A](a: A, result: ValidationResult): Boolean = {
      a match {
        case x: Option[_] => evaluateOption(x, result)
        case y: Seq[_] => evaluateSeq(y, result)
        case z: String => evaluateString(z, result)
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