@prefix : 'http://www.genyris.org/lib/gunit#'
@prefix u 'http://www.genyris.org/lang/utilities#'
@prefix type 'http://www.genyris.org/lang/types#'

include 'lib/gunit.g'
include 'lib/types-string.g'
include 'lib/classify.g'

def assert-classify(klass fixture)
  classify type:String fixture
  :assert (is-instance? fixture klass)

defmacro assert-url(fixture)
  template
    do
      classify type:String $fixture
      :assert (is-instance? $fixture type:URL)

do
  :test 'Not URLs'
    assert-classify Bignum 42
    assert-classify Symbol ^bar
    assert-classify Pair ^(1 2)
    assert-classify Dictionary (dict)

  :test 'Simple URL'
    assert-url 'http://www.genyris.org'
    assert-url 'http://www.genyris.org/'
