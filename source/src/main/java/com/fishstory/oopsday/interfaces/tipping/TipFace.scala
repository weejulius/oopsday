package com.fishstory.oopsday.interfaces.tipping

import unfiltered.filter.Plan
import unfiltered.request._
import collection.mutable.HashMap
import unfiltered.response.{ Found, Html }
import unfiltered.response.NotFound
import unfiltered.response.Status
import unfiltered.response.ResponseString
import unfiltered.response.Pass
import com.fishstory.oopsday.domain.tip.Tip

class TipFace extends Plan {
  private val _tips = new HashMap[Int, Tip]()
  private var _tip_generator: Int = 0

  _tips += (0 -> Tip.create(0, "Tip 0", "This is the tip 0", "jyu"))

  def intent = {
    case GET(Path("/tips")) => update_view(null)

    case GET(Path(Seg("tips" :: id :: Nil))) =>
      Found ~> index(id.toInt)

    case GET(Path(Seg("tips" :: id :: "edit" :: Nil))) =>
      update_view(_tips(id.toInt))

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

      index(_tip_id.toInt)
  }

  private def index(_tip_id: Int) = {
    val _tip = _tips(_tip_id)
    Html(
      <html>
        <title>Tips</title>
        <body>
          <ul class="tip">
            <li class="tip_id">{ _tip.id }</li>
            <li class="tip_title">{ _tip.title }</li>
            <li class="tip_content">{ _tip.content }</li>
            <li class="tip_author">{ _tip.author }</li>
          </ul>
        </body>
      </html>)
  }

  private def update_view(_tip: Tip) = {
    var _id: String = ""
    var _title: String = ""
    var _content: String = ""
    var _author: String = ""
    if (_tip != null) {
      _id = _tip.id.toString()
      _title = _tip.title
      _content = _tip.content
      _author = _tip.author
    }
    Html(

      <html>
        <head>
          <title>tip</title>
        </head>
        <body>
          <div id="container">
            <form method="POST" action="/tips">
              <input type="hidden" name="tip_id" value={ _id }/>
              <input name="tip_title" id="tip_title" value={ _title }/>
              <input name="tip_content" id="tip_content" value={ _content }/>
              <input name="tip_author" id="tip_author" value={ _author }/>
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