package com.fishstory.oopsday.interfaces.shared

import unfiltered.filter.Plan
import com.fishstory.oopsday.infrustructure.tip.Transactions
import unfiltered.filter.Intent
import com.fishstory.oopsday.interfaces.shared.validation.Validation

abstract class AbstractPlan extends Plan with Transactions with Validation {

  def intent = {

    val result = delegates
    result
  }

  def delegates: unfiltered.filter.Plan.Intent
}