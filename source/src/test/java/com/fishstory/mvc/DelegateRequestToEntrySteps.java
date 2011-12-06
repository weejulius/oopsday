package com.fishstory.mvc;

import cucumber.annotation.en.And;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en_au.When;

import static org.junit.Assert.assertEquals;

/**
 * User: Julius.Yu
 * Date: 12/6/11
 */
public class DelegateRequestToEntrySteps {

    private String contextPath;
    private String packageOfEntries;
    private String request;
    private String entryDelegatedTo;

    @And("^the context path is \"([^\"]*)\"$")
    public void the_context_path_is_(String a_contextPath) {
        contextPath = a_contextPath;
    }

    @And("^the package of entries is \"([^\"]*)\"$")
    public void the_package_of_entries_is_(String a_packageOfEntries) {
        packageOfEntries = a_packageOfEntries;
    }

    @Given("^a request is \"([^\"]*)\"$")
    public void a_request_is_(String a_request) {
        request = a_request;
    }

    @Then("^the request should be delegated into \"([^\"]*)\"$")
    public void the_request_should_be_delegated_into_(String expectedEntry) {
        assertEquals(expectedEntry, entryDelegatedTo);
    }

    @When("^the request is delegated$")
    public void the_request_is_delegated() {
        entryDelegatedTo = new RequestDelegation(contextPath,packageOfEntries).delegateTo(request);
    }
}
