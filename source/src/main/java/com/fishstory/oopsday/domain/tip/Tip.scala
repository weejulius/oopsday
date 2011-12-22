package com.fishstory.oopsday.domain.tip



class Tip(var _content:String) {

  if(_content.length() > Tip._maxNumberOfChar)
    throw new InvalidTipException()
  
  def content:String = _content
  
  def set_content(a_content:String)={ _content = a_content }
}

object Tip{
  
  private var _maxNumberOfChar = 20;
  
  def set_maxNumberOfChar(a_maxNumberOfChar:Int) = {_maxNumberOfChar = a_maxNumberOfChar}
}