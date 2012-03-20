package com.fishstory.oopsday.interfaces.shared.validation

case class Validations(a_params: Map[String, Seq[String]], a_validates: (String, String, List[Evaluation])*) {
  private var params: Map[String, Seq[String]] = Map.empty
  private var messages: Map[String, String] = Map.empty
  private var is_ever_failed = false

  params = a_params
  for (validate <- a_validates) {
    add(Validate(validate._1, validate._2, validate._3))
  }

  def result(): Result = {
    if (is_ever_failed) FAILURE(messages)
    else SUCCESS(messages)
  }

  private def add(validate: Validate) = {
    validate.setParam(params)
    val is_invalid = validate.is_invalid
    if (is_invalid) {
      is_ever_failed = true
      messages += (validate.parameter_name -> validate.messages.head)
    }
  }
}

case class StringValidations(a_validates: (String, String, List[Evaluation])*) {
  private var messages: Map[String, String] = Map.empty
  private var is_ever_failed = false

  for (validate <- a_validates) {
    add(Validate(validate._1, validate._2, validate._3))
  }

  def result(): Result = {
    if (is_ever_failed) FAILURE(messages)
    else SUCCESS(messages)
  }

  private def add(validate: Validate) = {
    val is_invalid = validate.is_invalid
    if (is_invalid) {
      is_ever_failed = true
      messages += (validate.parameter_name -> validate.messages.head)
    }
  }
}

case class Validate(parameter_name: String, alias: String, expressions: List[Evaluation]) {

  var messages: List[String] = List.empty
  var params: Map[String, Seq[String]] = Map.empty

  def setParam(a_params: Map[String, Seq[String]]) = params = a_params

  def is_invalid(): Boolean = {
    var is_ever_failed = false
    var last_expression_is_And_or_Or = true
    var is_the_first_expression = true

    for (expression <- expressions) {
      expression match {
        case And() => {
          if (last_expression_is_And_or_Or) {
            throw new IllegalArgumentException("And should not be the first expression")
          }
          if (is_ever_failed) {
            return is_ever_failed
          }
          last_expression_is_And_or_Or = true
        }
        case Or() => {
          if (last_expression_is_And_or_Or) {
            throw new IllegalArgumentException("Or should not be the first expression")
          }
          if (!is_ever_failed) {
            return is_ever_failed
          } else {
            is_ever_failed = false
            messages.drop(1)
          }

          last_expression_is_And_or_Or = true
        }
        case evaluation: Evaluation => {
          if (!is_the_first_expression && !last_expression_is_And_or_Or) {
            throw new IllegalArgumentException("expression should be after And or Or expression")
          }
          last_expression_is_And_or_Or = false
          is_the_first_expression = false

          evaluation match {
            case paramEvaluation: ParamEvaluation => paramEvaluation.setParams(params)
            case _ =>
          }

          is_ever_failed = !evaluation.evaluate(parameter_name)
          if (is_ever_failed) {
            messages = evaluation.message(alias) :: messages
          }
        }
      }
    }
    is_ever_failed
  }
}

abstract class Result {
  def get: Boolean
}

case class SUCCESS(messages: Map[String, String]) extends Result {
  def get = true
}

case class FAILURE(messages: Map[String, String]) extends Result {
  def get = false
}

abstract class Evaluation {
  def evaluate(name: String): Boolean

  def message(name: String): String
}

abstract class ParamEvaluation extends Evaluation {
  var params: Map[String, Seq[String]] = Map.empty

  def setParams(a_params: Map[String, Seq[String]]) = params = a_params
}

case class __IsEmpty extends ParamEvaluation {
  def evaluate(name: String) = {
    params.get(name).isEmpty || params(name).isEmpty || params(name).head.isEmpty
  }

  def message(name: String): String = "the " + name + " is not empty"
}

case class ParamIsNumeric extends ParamEvaluation {
  def evaluate(name: String) = {
    params(name).head.forall(_.isDigit)
  }

  def message(name: String) = {
    "the " + name + " is not numeric"
  }
}

case class __IsNumeric extends Evaluation {
  def evaluate(name: String) = {
    name != null && name.forall(_.isDigit)
  }

  def message(name: String) = {
    "the " + name + " is not numeric"
  }
}

case class __IsNotBlank extends ParamEvaluation {
  def evaluate(name: String) = {
    params.get(name).isDefined && !params(name).isEmpty && !params(name).head.isEmpty
  }

  def message(name: String): String = "the " + name + " is must"
}

case class And extends Evaluation {
  def evaluate(name: String): Boolean = true

  def message(name: String): String = ""
}

case class Or extends Evaluation {
  def evaluate(name: String): Boolean = true

  def message(name: String): String = ""
}

case class __MaxLength(a_max_length: Int) extends ParamEvaluation {
  def evaluate(name: String) = {
    params(name).head.length <= a_max_length
  }

  def message(name: String): String = "the " + name + " is more than " + a_max_length
}




