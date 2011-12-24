package com.fishstory.oopsday.interfaces.tipping
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.fishstory.oopsday.shared.The
import cucumber.annotation.en.Given
import cucumber.annotation.en.Then
import cucumber.annotation.en.When
import cucumber.annotation.Before
import unfiltered.jetty.Http
import cucumber.annotation.After

class Steps_Tip {
  private var _webDriver: WebDriver = new HtmlUnitDriver();
  private var _tipId: String = null;

  @Before
  def startServer = {
    Http(8080).plan(new TipFace).run
  }

  @Given("^the tip (\\d+) is exiting$")
  def the_tip_is_existing(a_tipId: String) {
    _tipId = a_tipId
  }

  @When("^the user open the link \"([^\"]*)\"$")
  def the_user_open_the_link(a_link: String) {
    _webDriver.get("http://localhost:8080/" + a_link)
  }

  @Then("^the user see \"([^\"]*)\"$")
  def the_user_should_see(a_content: String) {
    The string (_webDriver.findElement(By.id("tip_content")).getAttribute("value")) should_equal_to a_content
  }

  @After
  def topServer = {
    Http(8080).plan(new TipFace).stop()
  }

}