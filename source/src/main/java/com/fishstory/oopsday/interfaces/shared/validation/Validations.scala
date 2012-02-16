package com.fishstory.oopsday.interfaces.shared.validation

case class Validations(a_params: Map[String, Seq[String]], a_validates: Validate*) {
  private var params: Map[String, Seq[String]] = Map.empty
  private var messages: Map[String, String] = Map.empty
  private var is_ever_failed = false

  params = a_params
  for (validate <- a_validates) {
    add(validate)
  }

  def result(): Result = {
    if (is_ever_failed) FAILURE(messages)
    else SUCCESS(messages)
  }

  def add(validate: Validate) = {
    val is_invalid = validate.is_invalid(params)
    if (is_invalid) {
      is_ever_failed = true
      messages += (validate.parameter_name -> validate.messages.head)
    }
  }
}

case class Validate(parameter_name: String, alias: String, expressions: Expression*) {

  var messages: List[String] = List.empty

  def is_invalid(params: Map[String, Seq[String]]): Boolean = {
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
          }
          last_expression_is_And_or_Or = true
        }
        case m: Expression => {
          if (!is_the_first_expression && !last_expression_is_And_or_Or) {
            throw new IllegalArgumentException("expression should be after And or Or expression")
          }
          last_expression_is_And_or_Or = false
          is_the_first_expression = false
          is_ever_failed = !m.evaluate(params, parameter_name)
          if (is_ever_failed) {
            messages = m.message(alias) :: messages
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

abstract class Expression {
  def evaluate(a_params: Map[String, Seq[String]], name: String): Boolean
  def message(name: String): String
}

case class IsNotBlank extends Expression {
  def evaluate(a_params: Map[String, Seq[String]], name: String) = {
    a_params.get(name).isDefined && !a_params(name).isEmpty && !a_params(name).head.isEmpty
  }

  def message(name: String): String = "the " + name + " is must"
}

case class And extends Expression {
  def evaluate(a_params: Map[String, Seq[String]], name: String): Boolean = true
  def message(name: String): String = ""
}

case class Or extends Expression {
  def evaluate(a_params: Map[String, Seq[String]], name: String): Boolean = true
  def message(name: String): String = ""
}

case class MaxLength(a_max_length: Int) extends Expression {
  def evaluate(a_params: Map[String, Seq[String]], name: String) = {
    a_params(name).head.length <= a_max_length
  }

  def message(name: String): String = "the " + name + " is more than " + a_max_length
}




