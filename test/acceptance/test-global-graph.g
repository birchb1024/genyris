#
#
#

def test-an-exp(anExp)
   anExp
      .putprop ^type 'integer'
      .putprop ^type 'odd'
      .putprop ^colour 'black'
      .addprop ^country '61'
      .addprop ^country '44'
   assert
      equal? 'black'
         anExp(.getprop ^colour)
   assert
      equal? ^('44' '61')
         anExp(.getprop-list ^country)
   for t in ((anExp(.get-properties))(.asTriples))
      print t
test-an-exp ^symbol
test-an-exp 1
test-an-exp 'string'
test-an-exp (dict)
test-an-exp (graph)
test-an-exp (triple ^a ^b ^c)
 