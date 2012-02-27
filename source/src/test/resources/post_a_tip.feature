Feature: post a tip
   As a user I want to post a tip so I can share the tip
   
   Scenario: index a tip
   		Given the tip "1" is existing
   		When I am on the page "tips/1"
   		Then I should see the title "Tip 1"
   		And I should see the content "This is the tip 1"
   		
   		When I am on the page "tips/100"
      Then I should see the Not Found page
   		
   		When I am on the page "tips/1/100"
   		Then I should see the Not Found page

   Scenario: update a tip
        Given I am on the page "tips/1/edit"
        When I input the content "This is a modified tip 1"
        And I click the submit button
        Then I should see the content "This is a modified tip 1"
        And the URL should be "tips/1"
        
        Given I am on the page "tips/1/edit"
        When I input the tag "tag1,tag2"
        And I click the submit button
        Then I should see the tag "tag1tag2"
        
        Given I am on the page "tips/1/edit"
        When I input the tag "tag2,tag3"
        And I click the submit button
        Then I should see the tag "tag2tag3"
        
        Given I am on the page "tips/www/edit"
        Then I should see "Bad User Request"
        
        Given I am on the page "tips/100/edit"
        Then I should see the Not Found page

   Scenario: input a tip

        Given I am on the page "tips/new"
        When I click the submit button
        Then I should see the error message "the title is must"
        And I should see the error message "the content is must"
        And the URL should be "tips/new"

        Given I am on the page "tips/new"
        And I input the title "Tip 2"
        And I input the tag "Tag1"
        And I input the content "I am the content of tip 2"
        When I click the submit button
        Then I should see the title "Tip 2"
        And I should see the content "I am the content of tip 2"
        And I should see the tag "Tag1"
        And the URL should be "tips/2"
        
        Given I am on the page "tips/new"
        And I input the title "Tip 2"
        And I input the content "I am the content of tip 2"
        When I click the submit button
        Then I should see the title "Tip 2"
        And I should see the content "I am the content of tip 2"
        And the URL should be "tips/3"
        
        Given I am on the page "tips/new"
        And I input the title more than "150" characters
        And I input the content "I am the content of tip 2"
        When I click the submit button
        Then I should see the error message "the title is more than 120"
        And I should see the content "I am the content of tip 2"
   
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
        
        Given I have input "5" tips
        And the page size is "2"
        When I am on the page "tips"
        Then I should see "2" tips
        When I am on the page "tips?page=3"
        Then I should see "2" tips
        

