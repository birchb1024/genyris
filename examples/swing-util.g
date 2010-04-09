@prefix java 'http://www.genyris.org/lang/java#'
java:import 'java.awt.Color' as Color
java:import 'java.awt.Graphics' as Graphics
java:import 'javax.swing.JFrame' as JFrame
java:import 'org.genyris.java.swing.GPanel' as GPanel
GPanel
   var .new .new-org_genyris_interp_AbstractClosure # alias

def plot-window(title curve-function width height)
    var frame (JFrame(.new-java_lang_String title))
    def paint(graphics)
        tag Graphics graphics # tag with base class to get access to method
        cond
            graphics
                graphics
                    .setColor-java_awt_Color Color!red
                    curve-function (panel(.getWidth)) (panel(.getHeight))
    var panel (GPanel(.new paint))
    frame
       .setSize-int-int width height
       .add-java_awt_Component panel
       .setVisible-boolean true
