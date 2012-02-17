package com.fishstory.oopsday.interfaces.shared

/**
 * User: Julius Yu
 * Date: 2/17/12
 *
 */

case class PageNavigation(cur_page: Int, count_of_tips: Long, pageSize: Int) {
  val page_num: Int = calculate_page_num

  private def calculate_page_num: Int = {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("the page size is not valid")
    }
    if (count_of_tips < 0) {
      throw new IllegalArgumentException("the count is not less than 0")
    }
    val page_size_as_long = pageSize.toLong
    ((page_size_as_long + count_of_tips - 1) / page_size_as_long).toInt
  }
}
