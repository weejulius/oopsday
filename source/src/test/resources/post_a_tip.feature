Feature: post a tip
   As a user I want to post a tip so I can share the tip
   
   Scenario: index a tip
   		Given the tip "1" is existing
   		When I am on the page "tips/1"
   		Then I should see the title "Tip 1"
   		Then I should see the content "This is the tip 1"
   		
   		When I am on the page "tips/100"
      Then I should see the Not Found page
   		
   		When I am on the page "tips/1/100"
   		Then I should see the Not Found page

   Scenario: update a tip
        Given I am on the page "tips/1/edit"
        Then I should not be able to modify the author
        When I input the content "This is a modified tip 1"
        When I click the submit button
        Then I should see the content "This is a modified tip 1"
        And the URL should be "tips/1"
        
        Given I am on the page "tips/100/edit"
        Then I should see the Not Found page

   Scenario: input a tip
        Given I am on the page "tips/new"
        And I input the title "Tip 2"
        And I input the content "I am the content of tip 2"
        When I click the submit button
        Then I should see the title "Tip 2"
        Then I should see the content "I am the content of tip 2"
        And the URL should be "tips/2"
   
   Scenario: list tips
        Given I am on the page "tips"
        Then I should see the title "Tip 1"
        
        Given I am on the page "tips/new"
        And I input the title "Tip 3"
        And I input the content "This is  tip 3"
        And I click the submit button
        When I am on the page "tips"
        Then I should see the title "Tip 1"
        And I should see the title "Tip 3"

