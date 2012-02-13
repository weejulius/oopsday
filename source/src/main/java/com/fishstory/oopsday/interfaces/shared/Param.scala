package com.fishstory.oopsday.interfaces.shared

class Param {
  var validation_message = Map.empty[String, String]
  var is_voilated: Boolean = false
  var name: String = ""
  var params: Map[String, Seq[String]] = Map.empty

  def from(a_params: Map[String, Seq[String]]) = {
    params = a_params
    this
  }
  def ->(a_name: String) = {
    name = a_name
    is_voilated = false
    this
  }

  def is_defined = {
    is_voilated = !params.get(name).isDefined
    this
  }
  def is_not_empty = {
    is_defined
    is_voilated = !is_voilated && (params(name).isEmpty)
    this
  }

  def is_not_blank = {
    is_not_empty
    is_voilated = !is_voilated && (params(name)(0).isEmpty())
    this
  }
  def is_digit = {
    is_not_blank
    is_voilated = !is_voilated && params(name)(0).forall(_.isDigit)
    this
  }
  
  def is_empty_or_digit={
    
    if(!(params.get(name).isEmpty || params(name).isEmpty)){
       is_voilated = !params(name)(0).forall(_.isDigit)
    }
    this
  }

  def otherwise = this

  def mark(a_key: String, a_message: String) = {
    validation_message += (a_key -> a_message)

    this
  }

  def <=(a_name: String): String = {
    params(a_name)(0)
  }
}