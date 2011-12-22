package com.fishstory.oopsday.interfaces.tipping;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

import static org.junit.Assert.assertEquals;

/**
 * User: Julius.Yu
 * Date: 12/1/11
 */
public class PostingSteps {
    private String tip;
    private Tipping tipping = new Tipping();

    @Given("^I have input a tip \"([^\"]*)\"$")
    public void I_have_input_a_tip_(String the_tip) {
        tip = the_tip;
    }

    @When("^I post the tip$")
    public void I_post_the_tip() {
        tipping.post(tip);
    }

    @Then("^I can see the tip$")
    public void I_can_see_the_tip() {
        assertEquals("",tipping.look(1L));
    }
}
