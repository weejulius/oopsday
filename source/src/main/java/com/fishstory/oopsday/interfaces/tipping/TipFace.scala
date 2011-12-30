package com.fishstory.oopsday.interfaces.tipping

import unfiltered.filter.Plan
import unfiltered.request._
import collection.mutable.HashMap
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.response.{ NotFound, Found, Html }
import unfiltered.response.Redirect
import com.fishstory.oopsday.interfaces.shared.URL
import unfiltered.request.GET
import unfiltered.request.Seg

class TipFace extends Plan {
  private val _tips = new HashMap[Int, Tip]()

  _tips += (1 -> Tip.create("Tip 1", "This is the tip 1", "jyu"))

  def intent = {

    case req @ GET(Path("/tips")) => Scalate(req, "tip/tips.ssp", ("tips", _tips.values.toList))

    case req @ GET(Path("/tips/new")) => editable_page(req, None)

    case GET(Path(Seg("tips" :: key :: Nil))) =>

      var _tip: Option[Tip] = None

      if (is_a_id(key)) {
        _tip = _tips.get(key.toInt)
      } else {
        val parsedTitle: String = URL.decodeSpecialCharacters(key)
        _tip = findExistingTipByTitle(parsedTitle)
      }

      if (_tip.isDefined) {
        Found ~> index_page(_tip.get)
      } else {
        NotFound ~> not_found_page
      }

    case req @ GET(Path(Seg("tips" :: id :: "edit" :: Nil))) =>

      if (_tips.get(id.toInt).isDefined) {
        editable_page(req,Some(_tips(id.toInt)))
      } else {
        not_found_page
      }

    case POST(Path("/tips")) & Params(params) =>

      var _tip_id: String = params("tip_id")(0)
      var _tip: Tip = null

      if (_tip_id.isEmpty) {
        _tip = Tip.create(params("tip_title")(0), params("tip_content")(0), params("tip_author")(0))
        _tip_id = _tip.id.toString
      } else {
        _tip = _tips(_tip_id.toInt)
        _tip.update_content(params("tip_content")(0))
      }

      _tips.update(_tip_id.toInt, _tip)

      Redirect("/tips/" + _tip_id)
  }

  private def findExistingTipByTitle(a_title: String): Option[Tip] = {
    for (_el_tip <- _tips) {
      if (_el_tip._2.title.equalsIgnoreCase(a_title)) {
        return Some(_el_tip._2)
      }
    }
    None
  }

  private def is_a_id(input: String): Boolean = input.forall(_.isDigit)

  private def not_found_page = {
    Html(<html>
           <body>Not Found</body>
         </html>)
  }

  private def index_page(_tip: Tip) = {
    Html(
      <html>
        <title>Tips</title>
        <body>
          <ul class="tip">
            <li class="tip_title">
              { _tip.title }
            </li>
            <li class="tip_content">
              { _tip.content }
            </li>
            <li class="tip_author">
              { _tip.author }
            </li>
          </ul>
        </body>
      </html>)
  }

  private def editable_page(req: HttpRequest[Any], _tip: Option[Tip]) = {
    var _tip_properties: List[String] = List("", "", "", "")
    if (_tip.isDefined) {
      _tip_properties = List(_tip.get.id.toString, _tip.get.title, _tip.get.content, _tip.get.author)
    }
    Scalate(req, "tip/edit_tip.ssp", ("_tip_properties", _tip_properties))
  }
}

object TipFace {
  def create: TipFace = new TipFace()
}