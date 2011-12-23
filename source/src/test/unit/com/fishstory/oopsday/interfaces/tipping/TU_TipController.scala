package com.fishstory.oopsday.interfaces.tipping
import org.junit.Test
import org.junit.Before


class TU_TipController {
  
  private var tipController:TipController = null
  
  @Before
  def setUp = tipController = TipController.create
  
  @Test
  def index={
    tipController index 1
  }

}