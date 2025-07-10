@ns : 'http://www.genyris.org/lib/gunit#'
@ns u 'http://www.genyris.org/lang/utilities#'
@ns type 'http://www.genyris.org/lang/types#'
@ns render "http://www.genyris.org/lib/render#"

include 'lib/gunit.g'
include 'lib/types.g'
include 'lib/types-render.g'

do
   :test 'Render Atom'
     display (tag type:ProperAtom 42)!classes
     display
       (tag type:ProperAtom 42)
         .render:html
   :test 'Render Record'
     u:format "%x\n"
       (tag type:Record ^(1 'a' b))
          .render:html
   :test 'Render SequenceOfRecords'
     u:format "%x\n"
       (tag type:SequenceOfRecords ^((a b c)(1 2 3)))
          .render:html

