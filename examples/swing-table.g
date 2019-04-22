@prefix java 'http://www.genyris.org/lang/java#'
java:import 'javax.swing.JFrame' as JFrame
java:import 'javax.swing.JPanel' as JPanel
java:import 'javax.swing.JTable' as JTable
java:import 'javax.swing.JScrollPane' as JScrollPane
java:import 'java.awt.GridLayout' as GridLayout
java:import 'org.genyris.java.swing.GPanel' as GPanel
java:import 'org.genyris.java.swing.GenyrisTableModel' as GTable
java:import 'java.awt.Dimension' as Dimension
java:import 'javax.swing.ListSelectionModel' as ListSelectionModel
java:import 'javax.swing.DefaultListSelectionModel' as DefaultListSelectionModel

def getValueAt(row col)
   # print ("getValueAt: row %s col %s"(.format row col))
   * (+ row 1) (+ col 1)

def getColumnCount() 12

def getRowCount() 12

def setValueAt(row col value)
  print ("setValueAt: row %s col %s value = %s"(.format row col value))

def callback(&rest args)
   print ("callback: %s"(.format args))
               

def createAndShowGUI() 
        var frame (JFrame(.new-java_lang_String "Times Table"))

        var table 
           JTable
              .new-javax_swing_table_TableModel 
                   GTable(.new-org_genyris_interp_AbstractClosure callback)
        table
            .setPreferredScrollableViewportSize-java_awt_Dimension (Dimension(.new 200 200))
            .setFillsViewportHeight-boolean true
            .setCellSelectionEnabled-boolean true
            .setRowSelectionAllowed-boolean nil
            .setColumnSelectionAllowed-boolean nil
            #.setSelectionModel-javax_swing_ListSelectionModel(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        var newContentPane (JPanel(.new))
        newContentPane
            .setOpaque-boolean true # content panes must be opaque
            .setLayout-java_awt_LayoutManager (GridLayout(.new-int-int 1 0))
            .add-java_awt_Component table
        frame
            .setContentPane-java_awt_Container newContentPane
            .setSize-java_awt_Dimension (Dimension(.new-int-int 200 200))
            .pack
            .setVisible-boolean true
createAndShowGUI
