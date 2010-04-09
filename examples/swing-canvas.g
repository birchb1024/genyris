@prefix java 'http://www.genyris.org/lang/java#'
java:import 'java.awt.Color' as Color
java:import 'java.awt.Graphics' as Graphics
java:import 'javax.swing.JFrame' as JFrame
java:import 'javax.swing.JPanel' as JPanel
#
#
def main()
    var frame (JFrame(.new-java_lang_String "Oval Sample"))
    var panel (JPanel(.new))
    frame
       .setSize-int-int 300 200
       .add-java_awt_Component panel
       .setVisible-boolean true
    var graphics (panel(.getGraphics))
    tag Graphics graphics # tag with base class
    graphics
        #.setColor-java_awt_Color (Color(.getBlack))
        .drawOval-int-int-int-int 0 0 (panel(.getWidth)) (panel(.getHeight))
    frame
       .setVisible-boolean true
       sleep 5000
       .dispose
main

