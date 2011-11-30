package com.fishstory.oppsday.interfaces.tipping;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.junit.Assert.assertEquals;

/**
 * User: Julius.Yu
 * Date: 11/30/11
 */
public class user_post_a_tip_steps {

    private String tip;
    private Long id;
    private Tipping theTipping = new Tipping();


    @Given("I have input a tip $tip")
    public void  i_have_input(String a_tip){
        tip = a_tip;
    }

    @When("I post the tip")
    public void i_post_the_tip(){
         id = theTipping.post(tip);
    }

    @Then("I can see the tip")
    public void i_can_see_the_tip(){
         assertEquals(tip,theTipping.look(id));
    }
}
