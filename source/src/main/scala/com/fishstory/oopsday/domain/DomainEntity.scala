package com.fishstory.oopsday.domain

abstract class DomainEntity extends EntityAnnotationAlias with Valid {
  def isValid = true

  def id: Long
}