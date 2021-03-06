#
# Problem From RosettaCode 
#
# Parametric Polymorphism is a way to define types or functions that are generic over
# other types. The genericity can be expressed by using type variables for the parameter type, 
# and by a mechanism to explicitly or implicitly replace the type variables with concrete types 
# when necessary.
#
# Write a small example for a type declaration that is parametric over another type, 
# together with a short bit of code (and its type signature) that uses it. A good example 
# is a container type, let's say a binary tree, together with some function that traverses 
# the tree, say, a map-function that operates on every element of the tree.
#
#This language feature only applies to statically-typed languages. 
#
# Genyris is a dynamically typed language with optional type checking. 
# Parametric types are possible. Here's a simple function which mutiplies 
# two expressions of type "a" and returns a result with the same type: 
#
def times (a x y)
  is? x a                 # checks
  is? y a
  tag a (* x y)

#
#
#  RosettaCode example:
#

def squareBracket(type)
    .squareBracket type
         
class ParametricClass()
    define .T Thing
    def .squareBracket ((type = StandardClass))
        define .T type
        def .valid? (obj)
            .parametric-valid? .T obj  
      
class Tree(ParametricClass)
    def value (obj) (nth 0 obj)
    def lhs (obj) (nth 1 obj)
    def rhs (obj) (nth 2 obj)
    def replace (this value)
        rplaca this value 
    def .new (v l r)
        tag .self
            list v l r
    def .parametric-valid? (T obj)
        or
           null? obj
           and
                Tree(.valid? (lhs obj))
                Tree(.valid? (rhs obj))             
                is-instance? (value obj) T
    def .valid? (obj)
        .parametric-valid? .T obj               
    def .replaceAll ((value = .T))
        replace .self value       
        cond 
           (lhs .self)
              (lhs .self) (.replaceAll value)
        cond 
           (rhs .self)
              (rhs .self) (.replaceAll value)
        .self

class BignumTree(Tree)[Bignum]


BignumTree(.valid? ^(1 nil nil))
BignumTree
        .new 111 nil nil            
define someTree 
    BignumTree
        .new 23
            .new 22 nil nil
            .new 24 nil nil

someTree(.replaceAll 99)
assert 
   equal?
       someTree
       BignumTree
            .new 99
                .new 99 nil nil
                .new 99 nil nil
       

class StringTree(Tree)[String]
StringTree
    .new "str" nil nil

#error cases
catch error
  BignumTree
    .new "str" nil nil

catch error
   StringTree
     .new 234 nil nil

   

  