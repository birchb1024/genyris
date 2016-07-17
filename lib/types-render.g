@prefix type "http://www.genyris.org/lang/types#"
@prefix : "http://www.genyris.org/lib/render#"

do
   def atomTohtml(klass cell)
      template
          td((class=$klass)) $cell
   Bignum
      def .:html() (atomTohtml 'bignum' .self)
   String
      def .:html() (atomTohtml 'string' .self)
   Symbol
      def .:html() 
         template
             td((class='symbol')) 
               verbatim() $.self

type:Record
   def .:html()
      define guts nil
      for cell in .self # TODO replace with a map
         setq guts (append guts (list(cell(.:html))))
      template
         tr((class='record')) $guts
type:SequenceOfRecords
   def .:html()
      define guts nil
      for row in .self # TODO replace with a map
         setq guts (append guts (list(row (type:Record!:html))))
      template
         table((class='sequenceofrecords')) $guts
type:Table
type:HeadedTable
   def .:html()
      define first_row_guts ()
      for cell in (left .self) 
         setq first_row_guts (append first_row_guts (list(template (th() $cell))))
      define guts nil
      for row in (right .self)
         setq guts (append guts (list(row (type:Record!:html))))
      template
         table((class='headedtable')) 
            tr() $first_row_guts
            $guts
