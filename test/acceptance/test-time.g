#
# Time and date tests
#
@prefix date "http://www.genyris.org/lang/date#"

var epoch (dict (.am-pm = ^am) (.day-of-month = 1) (.day-of-week = 5) (.day-of-week-in-month = 1) (.day-of-year = 1) (.dst-offset = 0) (.era = ^AD) (.hour = 10) (.hour-of-day = 10) (.leap-year = nil) (.millisecond = 0) (.minute = 0) (.month = 0) (.second = 0) (.week-of-month = 1) (.week-of-year = 1) (.year = 1970) (.zone-offset = 10)) # Calendar Dictionary 
assert 
   equal?
      "%s" (.format (calendar 0))
      "%s" (.format epoch)

var expected (dict (.am-pm = ^pm) (.day-of-month = 8) (.day-of-week = 6) (.day-of-week-in-month = 2) (.day-of-year = 281) (.dst-offset = 3600000) (.era = ^AD) (.hour = 10) (.hour-of-day = 22) (.leap-year = ^nil) (.millisecond = 381) (.minute = 41) (.month = 9) (.second = 43) (.week-of-month = 2) (.week-of-year = 41) (.year = 2010) (.zone-offset = 10))
assert 
   equal?
      "%s" (.format (calendar 1286538103381))
      "%s" (.format expected)

assert
   equal?
      date:format-date 0 "dd MMM yyyy HH:mm:ss" "GMT"
      '01 Jan 1970 00:00:00'

assert
   equal?
      date:format-date 1286542403741 "dd MMM yyyy HH:mm:ss" "Australia/Melbourne"
      '08 Oct 2010 23:53:23'
      
assert
   equal?
      date:format-date (power 2 40) "dd MMM yyyy HH:mm:ss" "Australia/Melbourne"
      '04 Nov 2004 06:53:47'