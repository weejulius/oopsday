package com.fishstory.oopsday.interfaces.shared.validation


/**
 * Used to construct the validation expressions and evaluateOption it
 */
trait Validation {

  implicit def validate2Boolean[A](v: validate[A]): Boolean = v.isSatisfied

  case object isEmpty extends CommonEvaluation {

    def evaluateString(a: String): Boolean = a == null || a.isEmpty

    override def evaluateOption[A](a: Option[A]): Boolean =
      a.isEmpty || evaluate(a.get)

    override def evaluateSeq[A](a: Seq[A]): Boolean = a.isEmpty || evaluate(a.head)

  }

  case class MaxLength(length: Int) extends CommonEvaluation {

    def evaluateString(a: String): Boolean = a != null && a.length <= length

    override def additionalMessageValues: List[String] = List(length.toString)
  }

  case object notBlank extends CommonEvaluation {

    def evaluateString(a: String): Boolean = a != null && !a.isEmpty
  }

  case object isNumeric extends CommonEvaluation {

    def evaluateString(a: String): Boolean = a != null && a.length > 0 && a.forall(_.isDigit)
  }

  abstract class Evaluation {
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
  abstract class CommonEvaluation extends Evaluation {

    protected def additionalMessageValues: List[String] = Nil

    protected def evaluateString(a: String): Boolean

    protected def evaluateOption[A](a: Option[A]): Boolean = a.isDefined && evaluate(a.get)

    protected def evaluateSeq[A](a: Seq[A]): Boolean = !a.isEmpty && evaluate(a.head)

    private def evaluateWithMessageValue[A](a: A): List[String] = {
      var messageValues: List[String] = List.empty[String]
      a match {
        case x: Option[_] => if (!evaluateOption(x)) messageValues = pushMessageValue(x.get.toString)
        case y: Seq[_] => if (!evaluateSeq(y)) messageValues = pushMessageValue(y.toString())
        case z: String => if (!evaluateString(z)) messageValues = pushMessageValue(z.toString)
        case null => if (!evaluateString(null)) messageValues = pushMessageValue("null")
        case _ => throw new IllegalArgumentException("unsupported")
      }
      messageValues
    }

    private def pushMessageValue(value: String): List[String] = (value :: additionalMessageValues)

    protected def evaluate[A](a: A): Boolean = evaluateWithMessageValue(a).isEmpty

    def evaluate[A](value: A, result: ValidationResult = null): Boolean = {
      val messageValue: List[String] = evaluateWithMessageValue(value)
      val isSatisfied = messageValue.isEmpty
      if (result != null) {
        if (isSatisfied) result noMessageValue
        else result + messageValue
      }

      isSatisfied
    }
  }

  case class validate[A](var value: A) {

    private var currentEvaluation: Evaluation = null
    private var validationResult: ValidationResult = new ValidationResult()
    private var isCurrentEvaluationSatisfied = false
    private var hasFailedRound = false


    def using(_expression: Evaluation) = {
      currentEvaluation = _expression
      isCurrentEvaluationSatisfied = currentEvaluation.evaluate(value, validationResult)
      this
    }

    def and(newExpression: Evaluation) = {
      if (isCurrentEvaluationSatisfied) {
        using(newExpression)
      }
      this
    }

    def or(newExpression: Evaluation) = {
      if (!isCurrentEvaluationSatisfied) {
        validationResult.clearMessageValuesOfCurrentRound
        using(newExpression)
      }
      this
    }

    def andValidate(b: A) = {
      if (!isCurrentEvaluationSatisfied) hasFailedRound = true
      value = b
      validationResult.startNewRound()
      this
    }

    def isSatisfied = {
      !hasFailedRound && isCurrentEvaluationSatisfied
    }

    def result(): ValidationResult = validationResult
  }

}