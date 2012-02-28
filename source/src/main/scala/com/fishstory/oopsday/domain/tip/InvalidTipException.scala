package com.fishstory.oopsday.domain.tip

case class InvalidTipException(val message:String) extends RuntimeException(message){

}