
@prefix us "http://places.org/usa#"

var boston                   # variable declaration
  dict                       # new dictionary object
    .us:location = "Boston"
    .us:zip = "02110"
    .us:location-lat = 42.37
    .us:location-long = 71.03

# Library from culturevulture.org
@prefix c "http://culturevulture.org/styles#"

class c:EastCoast()
tag c:EastCoast boston # marks the object with a class

# Library from politics.com
@prefix pl "http://politics.com/leanings#"
class pl:Democrat()
tag pl:Democrat boston # marks the object with an additional class

assert
  and
    is-instance? boston pl:Democrat
    is-instance? boston c:EastCoast

print boston 
for c in (boston.classes)
  print c!name
