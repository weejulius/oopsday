package com.fishstory.oopsday.interfaces.tipping

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.fishstory.oopsday.shared.The
import cucumber.annotation.Before
import cucumber.annotation.After
import cucumber.annotation.en.{When, Given, Then}
import unfiltered.jetty.{Server, Http}
import com.fishstory.oopsday.domain.tip.Tip
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.WebElement
import com.fishstory.oopsday.infrustructure.Transactions
import com.fishstory.oopsday.domain.tip.TipRepository
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import scala.util.Random
import com.fishstory.oopsday.interfaces.shared.faceConfig

class Steps_Tip extends Transactions {
  private val _webDriver: WebDriver = new HtmlUnitDriver()
  private val _server: Server = Http(8080).plan(new TipFace)
  private val _tipRepository: TipRepository = new TipRepositoryJPAImpl()
  private val _origin_page_size = faceConfig.pageSize

  @Before
  def startServer = {
    _server.context("/resources") {
      _.resources(new java.net.URL(getClass().getResource("/css"), "."))
    }
    _server.start
  }

  @Given("^the tip \"([^\"]*)\" is existing$")
  def the_tip_is_existing(a_tip_id: String) {
    val tip = new Tip("Tip 1", "This is the tip 1", "jyu")
    transaction(_tipRepository.save_new_or_update(tip))
  }

  @Given("^I am on the page \"([^\"]*)\"$")
  def I_am_on_the_page(page: String) {
    _webDriver.get("http://localhost:8080/" + page)
  }

  @Given("^I input the content \"([^\"]*)\"$")
  def i_input_the_content(a_content: String) {
    _webDriver.findElement(By.id("tip_content")).clear()
    _webDriver.findElement(By.id("tip_content")).sendKeys(a_content)
  }

  @Given("^I input the title \"([^\"]*)\"$")
  def i_input_the_title(a_title: String) {
    _webDriver.findElement(By.id("tip_title")).clear()
    _webDriver.findElement(By.id("tip_title")).sendKeys(a_title)
  }

  @Given("^I input the id \"([^\"]*)\"$")
  def i_input_the_id(a_id: String) = {
    _webDriver.findElement(By.id("tip_id")).clear()
    _webDriver.findElement(By.id("tip_id")).sendKeys(a_id)
  }

  @Given("^I input the tag \"([^\"]*)\"$")
  def i_input_the_tag(a_tag: String) = {
    _webDriver.findElement(By.id("tip_tag")).clear()
    _webDriver.findElement(By.id("tip_tag")).sendKeys(a_tag)
  }

  @Given("^I input the title more than \"([^\"]*)\" characters$")
  def i_input_the_title_more_than(a_length: String) = {
    var chars = ""
    for (i <- 0 until a_length.toInt) {
      chars += (Random.nextPrintableChar())
    }
    _webDriver.findElement(By.id("tip_title")).clear()
    _webDriver.findElement(By.id("tip_title")).sendKeys(chars)
  }

  @Given("^I input the content more than \"([^\"]*)\" characters$")
  def i_input_the_content_more_than(a_length: String) = {
    var chars = ""
    for (i <- 0 until a_length.toInt) {
      chars += (Random.nextPrintableChar())
    }
    _webDriver.findElement(By.id("tip_content")).clear()
    _webDriver.findElement(By.id("tip_content")).sendKeys(chars)
  }

  @Given("^I have input \"([^\"]*)\" tips$")
  def i_have_input_tips(num_of_tips: String) = {
    for (i <- 0 until num_of_tips.toInt) {
      I_am_on_the_page("tips/new")
      i_input_the_title("I am a good title")
      i_input_the_content("I am a good content")
      i_click_the_submit_button()
    }
  }

  @Given("^the page size is \"([^\"]*)\"")
  def the_page_size_is(page_size: String) = {
    faceConfig.pageSize = page_size.toInt
  }

  @When("^I click the submit button$")
  def i_click_the_submit_button() {
    _webDriver.findElement(By.id("tip_submit")).submit()
  }

  @When("^I click the link \"([^\"]*)\"$")
  def i_click_the_link(a_link: String) {
    _webDriver.findElements(By.className("full_tip")).get(0).click()
  }

  @Then("^I should see the content \"([^\"]*)\"$")
  def i_should_see_the_content(a_content: String) {
    The list (getTextsFromElementsByClass("tip_content")) should_contain_the_element a_content
  }

  @Then("^I should see the title \"([^\"]*)\"$")
  def i_should_see_the_title(a_title: String) {
    The list (getTextsFromElementsByClass("tip_title")) should_contain_the_element a_title
  }

  @Then("^I should see the tag \"([^\"]*)\"$")
  def i_should_see_the_tag(a_tag: String) {
    The list (getTextsFromElementsByClass("tip_tag")) should_contain_the_element a_tag
  }

  @Then("^I should see the Not Found page$")
  def I_should_see_the_NOT_Found_page() {
    The string _webDriver.getPageSource should_contain "Not Found"
  }

  @Then("^the URL should be \"([^\"]*)\"$")
  def the_URL_should_be(a_url: String) {
    The string _webDriver.getCurrentUrl should_end_with a_url
  }

  @Then("^I should not be able to modify the author$")
  def i_should_not_be_able_to_modify_the_author() = {
    The string _webDriver.findElement(By.id("tip_author")).getAttribute("disabled") should_equal_to "true"
  }

  @Then("^I should see the error message \"([^\"]*)\"$")
  def i_should_see_the_error_message(message: String) {
    The string _webDriver.getPageSource() should_contain message
  }

  @Then("^I should see \"([^\"]*)\"$")
  def i_should_see(message: String) {
    The string _webDriver.getPageSource() should_contain message
  }

  @Then("^I should see \"([^\"]*)\" tips")
  def i_should_see_tips(num_of_tips: String) = {
    The number _webDriver.findElements(By.className("tip")).size().toLong should_equal_to num_of_tips.toInt
  }

  @Then("^I should see the content length of tip \"([^\"]*)\" is \"([^\"]*)\"$")
  def i_should_see_the_content_length_of_tip_is(a_title: String, a_length: String) {
    The number getElementByClassAndValue("tip_title", a_title).get.getText.length() should_not_be_greater_than_number a_length.toInt
  }

  @After
  def stopServer = {
    _server.stop()
    faceConfig.pageSize = _origin_page_size
  }

  private def getElementByClassAndValue(a_class_name: String, a_value: String): Option[WebElement] = {
    var iterator = _webDriver.findElements(By.className(a_class_name)).iterator
    while (iterator.hasNext) {
      var el = iterator.next()
      if (a_value != null && a_value.equals(el.getText())) {
        return Some(el)
      }
    }
    None
  }

  private def getTextsFromElementsByClass(a_class_name: String): List[String] = {
    var _titles = List[String]();

    var iterator = _webDriver.findElements(By.className(a_class_name)).iterator
    while (iterator.hasNext) {
      _titles = iterator.next.getText :: _titles
    }
    return _titles
  }

}