package com.fishstory.oopsday.interfaces.tipping

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.fishstory.oopsday.shared.The
import cucumber.annotation.Before
import cucumber.annotation.After
import cucumber.annotation.en.{When, Given, Then}
import unfiltered.jetty.{Server, Http}
import com.fishstory.oopsday.domain.tip.Tip

class Steps_Tip {
  private var _webDriver: WebDriver = null
  private var _tip: String = null
  private var _server: Server = null

  @Before
  def startServer = {
    _server = Http(8080).plan(new TipFace)
    _server.start
    _webDriver = new HtmlUnitDriver()
  }

  @Given("^the tip \"([^\"]*)\" is existing$")
  def the_tip_is_existing(a_tip: String) {
    _tip = a_tip
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

  @When("^I click the button$")
  def i_click_the_button() {
    _webDriver.findElement(By.id("tip_submit")).submit()
  }

  @Then("^I should see the content \"([^\"]*)\"$")
  def i_should_see_the_content(a_content: String) {
    The string (_webDriver.findElements(By.className("tip_content")).get(0).getText) should_equal_to a_content
  }

  @Then("^I should see the title \"([^\"]*)\"$")
  def i_should_see_the_title(a_title: String) {
    The string (_webDriver.findElements(By.className("tip_title")).get(0).getText) should_equal_to a_title
  }

  @Then("^I should see the Not Found page$")
  def I_should_see_the_NOT_Found_page() {
    The string _webDriver.getPageSource() should_contain "Not Found"
  }
  
  @Then("^the URL should be \"([^\"]*)\"$")
  def the_URL_should_be(a_url:String) {
    The string _webDriver.getCurrentUrl() should_end_with a_url
  }

  @After
  def stopServer = {
    _server.stop()
    Tip.reset_tip_generator
    _webDriver.quit()
  }

}