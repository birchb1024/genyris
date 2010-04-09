#
#  Example Swing program using Java FFI
#
@prefix java 'http://www.genyris.org/lang/java#'
@prefix u "http://www.genyris.org/lang/utilities#"

java:import 'javax.swing.JFrame' as JFrame
java:import 'java.awt.Dimension' as Dimension
java:import 'javax.swing.JTextField' as JTextField
java:import 'javax.swing.JButton' as JButton
java:import 'java.awt.FlowLayout' as FlowLayout
java:import 'javax.swing.ButtonModel' as ButtonModel

var window
   JFrame(.new-java_lang_String "Welcome to Genyris - examples/swing.g")

window
  .setSize-java_awt_Dimension (Dimension(.new-int-int 400 200))

java:import (window(.getContentPane))!java:class # need the class first
var cp (window(.getContentPane))

(var field (JTextField(.new)))
    .setPreferredSize-java_awt_Dimension (Dimension(.new-int-int 150 20))

def onCancel(event)
    print 'Cancel button pressed'
    window(.dispose)

var counter 0

def onHit(event)
    print 'Hit button pressed'
    counter = (+ 1 counter)
    field
       .setText-java_lang_String counter
    
var aButton (JButton(.new))
aButton
   .setPreferredSize-java_awt_Dimension (Dimension(.new-int-int 100 20))
   .setText-java_lang_String 'Press Me'
   .addActionListener-java_awt_event_ActionListener (java:actionListener onHit)

var cancelButton (JButton(.new))
cancelButton
   .setPreferredSize-java_awt_Dimension (Dimension(.new-int-int 100 20))
   .setText-java_lang_String 'Cancel'
   .addActionListener-java_awt_event_ActionListener (java:actionListener onCancel)

cp
   .add-java_awt_Component field
   .add-java_awt_Component aButton
   .add-java_awt_Component cancelButton
   .setLayout-java_awt_LayoutManager (FlowLayout(.new))
   
window
  .pack
  .show

