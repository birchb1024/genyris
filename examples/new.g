## Copyright 2008 Peter William Birch <birchb@genyis.org>
##
## This software may be used and distributed according to the terms
## of the Genyris License, in the file "LICENSE", incorporated herein by reference.
##
###
### Examples of constructors
###

##
##  Simple constructor in a class
##
class Person ()
   def .new (name date-of-birth)
      dict
         .name = name
         .dob = date-of-birth
         .classes = (list Person)


define jo
   (Person.new) "Jo" 23
assert
   equal? (jo.name) "Jo"
##
##  Derived class calls super-class constructor
##
class Employee (Person)
   def .new (name date-of-birth salary-number)
      ((Person.new) name date-of-birth)
          define .salary-number salary-number
          define .classes (list Employee)
          .self

define fred
   Employee!new "Fred Smith" "1/1/1900" 123123123

assert
   equal? fred!dob "1/1/1900"
##
## Add to an existing object to make it belong to a new class.
##
class Manager (Employee)

  def .new (employee grade)
     is? employee Employee
     employee
        define .managment-grade grade
     tag Manager employee

# promote fred
Manager!new fred "Project Mangler"

assert
   equal? fred!managment-grade "Project Mangler"
