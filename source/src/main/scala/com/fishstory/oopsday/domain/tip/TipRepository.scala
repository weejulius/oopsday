package com.fishstory.oopsday.domain.tip


trait TipRepository {

  def find_by_id_is(id: Long): Option[Tip]

  def find_all(start: Int, size: Int): java.util.List[Tip]

  def find_by_title_is(title: String): Option[Tip]

  def save_new_or_update(tip: Tip): Tip

  def count(): Long


}