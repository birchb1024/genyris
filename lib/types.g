@prefix : "http://www.genyris.org/lang/types#"

class :ProperAtom()
   # atomic atoms !
   def .valid?(obj)
       or
          is-instance? obj String
          is-instance? obj Bignum
          is-instance? obj Symbol

class :Record()
   # a list of proper atoms
   def .valid?(obj)
      cond
         (is-instance? obj Pair)
            cond
               obj!right  # end of the list?
                  and
                      :ProperAtom!valid? obj!left
                      :Record!valid? obj!right
               else
                  :ProperAtom!valid? obj!left
   def .same-type?(type record)
      and
         is-instance? record!left type
         cond
            record!right  # more in the list?
               :Record!same-type? type record!right
            else
               true
   def .unique?(record)
      and
         not (member? record!left record!right) 
         cond
            record!right  # more in the list?
               :Record!unique? record!right
            else
               true

class :SequenceOfRecords()
   # A list of records
   def .valid?(obj)
      cond
         (is-instance? obj Pair)
            cond
               obj!right  # end of the list?
                  and
                      :Record!valid? obj!left
                      :SequenceOfRecords!valid? obj!right
               else
                  :Record!valid? obj!left

class :Table(:SequenceOfRecords)
   # A list of equal-length records
   def .valid?(obj)
      define mylength (length obj!left)
      cond
         obj!right
            cond
                (equal? mylength (:Table!valid? obj!right))
                    mylength
         else   # end of the list
            mylength
   # method returns the width of the table
   def .width()
      length (.self .left)
      
class :HeadedTable(:Table)
   # A list of equal-length records with a first-row header of unique strings or symbols
   def .valid?(obj)
      and
         :Record!unique? obj!left
         or
            :Record!same-type? String obj!left
            :Record!same-type? Symbol obj!left

