#Story

As a user I want to post a tip so that I can share the tip with others.

Scenario: post a tip

Given I have input a tip "This is a very useful tip"
When I post the tip
Then I can see the tip
