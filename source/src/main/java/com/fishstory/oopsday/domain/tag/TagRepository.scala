package com.fishstory.oopsday.domain.tag

abstract class TagRepository {
  
  def find_by_name_or_save_new(name:String):Tag

}