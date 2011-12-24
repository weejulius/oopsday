package com.fishstory.oopsday.interfaces.tipping
import org.junit.Test
import org.junit.Before


class TU_TipController {
  
  private var tipController:TipFace = null
  
  @Before
  def setUp = tipController = TipFace.create
  
  @Test
  def index={
    
  }

}