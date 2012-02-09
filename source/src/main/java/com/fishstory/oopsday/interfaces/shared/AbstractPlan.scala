package com.fishstory.oopsday.interfaces.shared

import unfiltered.filter.Plan
import com.fishstory.oopsday.infrustructure.tip.Transactions
import unfiltered.filter.Intent

abstract class AbstractPlan extends Plan with Transactions {

  def intent = {
   
    val result = delegate
    result
  }

  def delegate: unfiltered.filter.Plan.Intent
}