package com.fishstory.oopsday.interfaces.tipping

import unfiltered.filter.Plan
import unfiltered.request._
import collection.mutable.HashMap
import unfiltered.response.{Found, Html}


class TipFace extends Plan {
  private val _tips = new HashMap[Int, String]()
  private var _tip_generator: Int = 0

  _tips += (0 -> "This is the tip 0")

  def intent = {
    case GET(Path("/tip")) => update_view("", "")

    case GET(Path(Seg("tip" :: id :: Nil))) =>
      Found ~> index(id.toInt)


    case GET(Path(Seg("tip" :: id :: "edit" :: Nil))) =>
      update_view(id, _tips(id.toInt))

    case POST(Path("/tip")) & Params(params) =>
      var _tip_id: String = params("tip_id")(0)

      if (_tip_id == "") {
        _tip_generator = _tip_generator + 1
        _tip_id = _tip_generator.toString
      }
      _tips.update(_tip_id.toInt, params("tip_content")(0))

      index(_tip_id.toInt)
  }

  private def index(id: Int) = {
    Html(
      <html>
        <title>1</title>
        <body>
          <h1 id="tip_content">
            {_tips(id)}
          </h1>
        </body>
      </html>
    )
  }

  private def update_view(_tip_id: String, _content: String) = Html(
    <html>
      <head>
        <title>tip</title>
      </head>
      <body>
        <div id="container">
          <form method="POST" action="/tip">
              <input type="hidden" name="tip_id" value={_tip_id}/>
              <input name="tip_content" id="tip_content" value={_content}/>
              <input type="submit" id="tip_submit" value="share"/>
          </form>
        </div>
      </body>
    </html>
  )

}

object TipFace {
  def create: TipFace = new TipFace()
}