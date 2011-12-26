package com.fishstory.oopsday.interfaces.tipping

import unfiltered.filter.Plan
import unfiltered.request._
import collection.mutable.HashMap
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.response.{NotFound, Found, Html}

class TipFace extends Plan {
  private val _tips = new HashMap[Int, Tip]()
  private var _tip_generator: Int = 0

  _tips += (0 -> Tip.create(0, "Tip 0", "This is the tip 0", "jyu"))

  def intent = {

    case GET(Path("/tips")) => editable_page(None)

    case GET(Path(Seg("tips" :: key :: Nil))) =>

      var _tip: Option[Tip] = null

      if (isId(key)) {
        _tip = _tips.get(key.toInt)
      } else {
        _tip = _tips.filter(!_._2.title.equalsIgnoreCase(key)).get(0)
      }

      if (_tip.isEmpty) {
        NotFound ~> Html(<html>
          <body>Not Found</body>
        </html>)
      } else {
        Found ~> index_page(_tip.get)
      }

    case GET(Path(Seg("tips" :: id :: "edit" :: Nil))) =>
      editable_page(Some(_tips(id.toInt)))

    case POST(Path("/tips")) & Params(params) =>
      var _tip_id: String = params("tip_id")(0)
      var _tip: Tip = null

      if (_tip_id.isEmpty()) {
        _tip_generator = _tip_generator + 1
        _tip_id = _tip_generator.toString
        _tip = Tip.create(_tip_generator, params("tip_title")(0), params("tip_content")(0), params("tip_author")(0))
      } else {
        _tip = _tips(_tip_id.toInt)
        _tip.update_content(params("tip_content")(0))
      }

      _tips.update(_tip_id.toInt, _tip)

      index_page(_tip)
  }

  private def isId(input: String): Boolean = input.forall(_.isDigit)

  private def index_page(_tip: Tip) = {
    Html(
      <html>
        <title>Tips</title>
        <body>
          <ul class="tip">
            <li class="tip_id">
              {_tip.id}
            </li>
            <li class="tip_title">
              {_tip.title}
            </li>
            <li class="tip_content">
              {_tip.content}
            </li>
            <li class="tip_author">
              {_tip.author}
            </li>
          </ul>
        </body>
      </html>)
  }

  private def editable_page(_tip: Option[Tip]) = {
    var _tip_properties: List[String] = List("", "", "", "")
    if (_tip.isDefined) {
      _tip_properties = List(_tip.get.id.toString, _tip.get.title, _tip.get.content, _tip.get.author)
    }

    Html(
      <html>
        <head>
          <title>tip</title>
        </head>
        <body>
          <div id="container">
            <form method="POST" action="/tips">
                <input type="hidden" name="tip_id" value={_tip_properties(0)}/>
                <input name="tip_title" id="tip_title" value={_tip_properties(1)}/>
                <input name="tip_content" id="tip_content" value={_tip_properties(2)}/>
                <input name="tip_author" id="tip_author" value={_tip_properties(3)}/>
                <input type="submit" id="tip_submit" value="share"/>
            </form>
          </div>
        </body>
      </html>)
  }
}


object TipFace {
  def create: TipFace = new TipFace()
}