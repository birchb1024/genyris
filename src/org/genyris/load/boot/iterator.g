#
# Iterators
#
@prefix sys "http://www.genyris.org/lang/system#"


# semantic tags
class Iterator(EagerProcedure)
class PairIterator(Iterator)
class RangeIterator(Iterator)
class DictionaryKeyIterator(Iterator)
class ReaderLineIterator(Iterator)


Thing
#   def .mkIterator() 
#      raise 'Iterator not defined for class'

NilSymbol
   def .mkIterator() 
      lambda() ^sys:StopIteration

Pair
   def .mkIterator()
      define head .self
      tag PairIterator 
         lambda()
             cond
                (null? head) ^sys:StopIteration
                else
                   var result (left head)
                   setq head (right head)
                   result
Dictionary
   def .mkIterator()
      tag DictionaryKeyIterator
         .vars
            .mkIterator

Reader
   def .mkIterator()
      define reader .self
      tag ReaderLineIterator 
         function()
             cond
                (reader(.hasData))
                   reader(.getline)
                else
                   ^sys:StopIteration
      
Graph
   def .mkIterator()
        (.asTriples)
            .mkIterator
                   
