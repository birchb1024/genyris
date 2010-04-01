#
# Tests on triples
#
def test-equality()
   assert
      equal?
          triple ^X ^O 89
          triple ^X ^O 89
   assert
      not
          equal?
              triple ^(1=2) ^O ^(w = 45)
              triple ^(1=2) ^O ^(w = 45)
   var someDict (dict (.a = 3))
   assert
      equal?
          triple someDict ^O 89
          triple someDict ^O 89
   var eighty 80
   assert
      equal?
           triple ^X ^O eighty
           triple ^X ^O eighty
   assert
      not
         equal?
              triple ^X ^O eighty
              triple ^X (gensym "O") eighty

test-equality

def test-remove()
   var ts (triplestore)
   ts
     .add (triple ^s ^d ^f)
   assert
      equal?
         ts(.asTriples)
         list (triple ^s ^d ^f)

   ts(.add (triple ^z^x^c))
   print
      ts(.asTriples)
   print
      ts(.asTriples)
   print
      ts(.asTriples)
   assert
      (SetList.equal?)
         ts(.asTriples)
         list (triple ^s ^d ^f) (triple ^z ^x ^c)

   ts(.remove (triple ^s ^d ^f))
   assert
      equal?
         ts(.asTriples)
         list (triple ^z ^x ^c)

def test-select()
   var joe "Joe"
   var ts
      triplestore 
         ~ ^(joe age three)
         ~ ^(joe age ten)
         ~ ^("John" age 22)
         ~ ^("John" height 223)
   var result
      ts
        .select ^joe nil nil
   print
       result(.asTriples)
   assert
      equal?
         result
         triplestore ^(joe age three) ^(joe age ten)
   assert
      equal?
         result
         triplestore  ^(joe age ten)  ^(joe age three)

def test-get()
   var ts
      triplestore 
         ~ ^(joe age ten)
         ~ ^(joe height 22)
         ~ ^("John" age 223)
   var result
      ts
        .get ^joe ^age
   assert (equal? result ^ten)

def test-get-list()
   var ts
      triplestore 
         ~ ^(joe friend adam)
         ~ ^(joe height 22)
         ~ ^(joe friend bruce)
   var result
      ts
        .get-list ^joe ^friend
   assert 
        or
            equal? result ^(adam bruce)
            equal? result ^(bruce adam)

def test-put()
   var ts
      triplestore 
         ~ ^(joe age ten)
         ~ ^(joe age 11)
         ~ ^(joe age 12)
         ~ ^(joe age 13)
         ~ ^(joe height 22)
         ~ ^("John" age 223)
   var result
      ts
        .put ^joe ^age 98
   assert (equal? (ts(.length)) 3)

test-remove
test-select
test-get
test-get-list
test-put