package com.fishstory.oopsday.domain

abstract class Entity extends EntityAnnotationAlias with Valid {
  def isValid = true
}