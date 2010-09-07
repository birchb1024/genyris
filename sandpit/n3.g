#
# Comment
#
@prefix p "http://www.example.org/personal_details#"
@prefix m "http://www.example.org/meeting_organization#"
@prefix person "http://www.example.org/people#"


person:fred
  p:GivenName = "Fred"
  p:hasEmail = "mailto:fred@example.com"

var fred
  graph
   ^(person:fred p:GivenName "Fred")
   ^(person:fred p:hasEmail "mailto:fred@example.com")
   #

