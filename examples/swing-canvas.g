@prefix java 'http://www.genyris.org/lang/java#'
java:import 'java.awt.Color' as Color
java:import 'java.awt.Graphics' as Graphics
java:import 'javax.swing.JFrame' as JFrame
java:import 'org.genyris.java.swing.GPanel' as GPanel
#
#
def main()
    var frame (JFrame(.new-java_lang_String "Oval Sample"))
    def repaint(graphics id command)
        tag Graphics graphics # tag with base class to get access to method
        cond
            graphics
                graphics
                    .setColor-java_awt_Color Color!red
                    .drawOval-int-int-int-int 0 0 (panel(.getWidth)) (panel(.getHeight))
    var panel (GPanel(.new-org_genyris_java_swing_GenyrisActionListener (java:actionListener repaint)))
    frame
       .setSize-int-int 300 200
       .add-java_awt_Component panel
       .setVisible-boolean true
       sleep 10000
       .dispose
main

