package com.fishstory.oopsday.interfaces.shared.validation


/**
 * Used to construct the validation expressions and evaluateOption it
 */
trait Validation {

  implicit def validate2Boolean[A](v: validate[A]): Boolean = v.isSatisfied

  case object isEmpty extends CommonExpression {

    def evaluateString(a: String): Boolean = a == null || a.isEmpty

    override def evaluateOption[A](a: Option[A]): Boolean =
      a.isEmpty || evaluate(a.get)

    override def evaluateSeq[A](a: Seq[A]): Boolean = a.isEmpty || evaluate(a.head)

  }

  case class MaxLength(length: Int) extends CommonExpression {

    def evaluateString(a: String): Boolean = a != null && a.length <= length

    override def additionalMessageValues: List[String] = List(length.toString)
  }

  case object notBlank extends CommonExpression {

    def evaluateString(a: String): Boolean = a != null && !a.isEmpty
  }

  case object isNumeric extends CommonExpression {

    def evaluateString(a: String): Boolean = a != null && a.length > 0 && a.forall(_.isDigit)
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

    protected def additionalMessageValues: List[String] = Nil

    protected def evaluateString(a: String): Boolean

    protected def evaluateOption[A](a: Option[A]): Boolean = a.isDefined && evaluate(a.get)

    protected def evaluateSeq[A](a: Seq[A]): Boolean = !a.isEmpty && evaluate(a.head)

    private def evaluateWithMessageValue[A](a: A): List[String] = {
      var messageValues: List[String] = List.empty[String]
      a match {
        case x: Option[_] => if (!evaluateOption(x)) messageValues = (x.get.toString :: additionalMessageValues)
        case y: Seq[_] => if (!evaluateSeq(y)) messageValues = (y.toString() :: additionalMessageValues)
        case z: String => if (!evaluateString(z)) messageValues = (z :: additionalMessageValues)
        case null => if (!evaluateString(null)) messageValues = ("null" :: additionalMessageValues)
        case _ => throw new IllegalArgumentException("unsupported")
      }
      messageValues
    }

    protected def evaluate[A](a: A): Boolean = evaluateWithMessageValue(a).isEmpty

    def evaluate[A](value: A, result: ValidationResult = null): Boolean = {
      val messageValue: List[String] = evaluateWithMessageValue(value)
      val isSatisfied = messageValue.isEmpty
      if (result != null) {
        if (isSatisfied) result addEmpty
        else result + messageValue
      }

      isSatisfied
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