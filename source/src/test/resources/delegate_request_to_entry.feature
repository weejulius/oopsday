Feature: delegate request to corresponding entry
     For example: the request "domain.com/tip/1" will be delegated into the entry TipEntry

     Scenario: delegate request
     Given a request is "http://www.domain.com/tip/1"
     And the context path is "/"
     And the package of entries is "com.fishstory.oopsday.entry"
     When the request is delegated
     Then the request should be delegated into "com.fishstory.oopsday.entry.TipEntry"

     Scenario: entry is a character
     Given a request is "/t/1"
     And the context path is "/"
     And the package of entries is "com.fishstory.oopsday.entry"
     When the request is delegated
     Then the request should be delegated into "com.fishstory.oopsday.entry.TEntry"

     Scenario: request is relative path
     Given a request is "/tip/1"
     And the context path is "/"
     And the package of entries is "com.fishstory.oopsday.entry"
     When the request is delegated
     Then the request should be delegated into "com.fishstory.oopsday.entry.TipEntry"