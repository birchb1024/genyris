@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix u 'http://www.genyris.org/lang/utilities#'
@prefix type 'http://www.genyris.org/lang/types#'

include 'lib/gunit.g'
include 'lib/types.g'

do
   :test 'ProperAtom s'
     :assert (type:ProperAtom!valid? 42)
     :assert (type:ProperAtom!valid? 'foo')
     :assert (type:ProperAtom!valid? ^bar)
     :assertNil (type:ProperAtom!valid? ^(a b))
     :assertNil (type:ProperAtom!valid? (dict))
   :test 'Records'
     :assertNil (type:Record!valid? 1)
     :assertNil (type:Record!valid? 'a')
     :assertNil (type:Record!valid? ^b)
     :assertNil (type:Record!valid? (dict))
     :assertNil (type:Record!valid? ^((1)))
     :assertNil (type:Record!valid? nil)
     :assert (type:Record!valid? ^(1 2 3))
     :assert (type:Record!valid? ^(1 'a' c))
   :test 'Records .same-type?'
     :assert (type:Record!same-type? Bignum ^(1 2 3))
     :assert (type:Record!same-type? String ^('a' 'a' 'c'))
     :assert (type:Record!same-type? Symbol ^(a b c))
     :assertNil (type:Record!same-type? Bignum ^(1 b))
     :assertNil (type:Record!same-type? String ^(1 'c'))
     :assertNil (type:Record!same-type? Symbol ^(b 'd'))

   :test 'Records .unique?'
     :assert (type:Record!unique? ^(1 2 3))
     :assert (type:Record!unique? ^('a' 'b' 'c'))
     :assert (type:Record!unique? ^(a b c))
     :assertNil (type:Record!unique? ^(1 1))
     :assertNil (type:Record!unique? ^('c' 'c'))
     :assertNil (type:Record!unique? ^(b b))
    
   :test 'Sequence of Records'
     :assertNil (type:SequenceOfRecords!valid? 1)
     :assertNil (type:SequenceOfRecords!valid? 'a')
     :assertNil (type:SequenceOfRecords!valid? ^b)
     :assertNil (type:SequenceOfRecords!valid? (dict))
     :assertNil (type:SequenceOfRecords!valid? ^((1) 1))
     :assertNil (type:SequenceOfRecords!valid? ^((1) 'a'))
     :assertNil (type:SequenceOfRecords!valid? ^((1) b))
     :assertNil (type:SequenceOfRecords!valid? ^(nil))
     :assert (type:SequenceOfRecords!valid? ^((1 'a' b)))
     :assert (type:SequenceOfRecords!valid? ^(('a')))
     :assert (type:SequenceOfRecords!valid? ^((b)))

   :test 'Table'
     :assertEqual 1 (type:Table!valid? ^((1)))
     :assertEqual 2 (type:Table!valid? ^((1 2)))
     :assertEqual 3 (type:Table!valid? ^((1 'a' b)))
     :assertEqual 3 (type:Table!valid? ^((1 'a' b)(1 2 3)))
     :assertEqual 3 (type:Table!valid? ^((1 'a' b)(4 5 6)(6 7 8)))
     :assertNil (type:Table!valid? ^((1 a)(2)))
     :assertNil (type:Table!valid? ^((1 a)(2 3)(4 5 6)))
     :assertNil (type:Table!valid? ^((1 a)(2 2)(4)))

include 'lib/classify.g'

def classify-test(klass fixture)
   classify type:SequenceOfRecords fixture
   :assert (is-instance? fixture klass)

define a-table ^((1 'a' b)(1 2 3)(1 2 3))

do
   :test 'Classify Tables'
      classify-test type:SequenceOfRecords ^((1)(2 3))
      classify-test type:SequenceOfRecords ^((1)(2 3)(4 5 6))
      classify-test type:SequenceOfRecords ^((1 1)(2 3)(4 5 6))
      classify-test type:Table ^((1))
      classify-test type:Table ^((1)(2))
      classify-test type:Table ^((1 2)(3 4))
      classify-test type:Table ^(('a' 'b')(1 2))
      classify-test type:Table ^(('a' 'a' b)(1 2 3)(1 2 3))
      classify-test type:HeadedTable ^((x))
      classify-test type:HeadedTable ^((x)(2))
      classify-test type:HeadedTable ^((x y)(3 4))
      classify-test type:HeadedTable ^(('x' 'y')(1 2))
      classify-test type:HeadedTable ^((x y z)(1 2 3)(1 2 3))
do
   :test 'Tables width method'
      define a-table ^((1 'a' b)(1 2 3)(1 2 3))
      classify type:SequenceOfRecords a-table
      :assert (is-instance? a-table type:Table) 
      :assertEqual 3 (a-table(.width))
