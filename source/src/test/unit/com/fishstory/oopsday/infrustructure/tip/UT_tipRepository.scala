package com.fishstory.oopsday.infrustructure.tip

import com.fishstory.oopsday.domain.tip.Tip
import org.junit.Test
import org.junit.Assert._
import com.fishstory.oopsday.infrustructure.{Transactions, tipRepository}
import reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: julius.yu
 * Date: 3/23/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */

class UT_tipRepository extends Transactions {


  @Test
  def test {
    transaction {
      assertEquals(1, tipRepository.saveNewOrUpdate(new Tip("a", "b", "c")).id)
      assertEquals("b", tipRepository.column("title", "a").get content)
      assertEquals("b", tipRepository.id(1).get content)
      assertEquals(1, tipRepository.count())

      tipRepository.save(new Tip("abc", "bc", "c")) when tipRepository.column("title", "abc").isEmpty
      tipRepository.save(new Tip("abcd", "d", "c")) when tipRepository.column("title", "abc").isEmpty
      assertEquals("bc", tipRepository.column("title", "abc").get content)
      assertTrue(tipRepository.column("title", "abcd").isEmpty)

      //tipRepository.saveIfNotFound(new Tip("abcd","d","c"))

      //find the[Tip]().title equals("a")
      //find the[Tip]() is (new Tip("a","b","c"))

    }
  }

    @Test
    def testPrint{
     // print[Tip]()
      print1(new Tip().title)
     // table[Tip].select column("title") is("b")

    }

    class a{
      def title=(a:String)

    }



    def print1(a: =>Any)=println(a.toString())
    def print[A]()(implicit  a:Manifest[A]){
      for(one:java.lang.reflect.Method   <- a.erasure.getMethods){
        one.setAccessible(true)
        println(one.toString())
      }
    }

}
