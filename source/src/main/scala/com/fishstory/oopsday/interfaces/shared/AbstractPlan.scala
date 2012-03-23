package com.fishstory.oopsday.interfaces.shared

import unfiltered.filter.Plan
import com.fishstory.oopsday.interfaces.shared.validation.Validation
import com.fishstory.oopsday.infrustructure.Repository

abstract class AbstractPlan extends Plan with Repository with Validation {

  def intent = {

    val result = delegates
    result
  }

  def delegates: Plan.Intent
}