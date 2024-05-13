import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.scene.control.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.scene.control.{DatePicker, Tab, TabPane}

import java.time.*

object Main extends JFXApp3:

  val UIwidth = 1500
  val UIheight = 1000
  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "Learning Diary"
      width = UIwidth
      height = UIheight

    // MAIN TABPANE
    val root = new TabPane()

    // Entry Pane
    val tab1 = new EntryInput(UIheight,UIwidth)
    tab1.text = "Entry Input"

    val tab2 = new MonthlyView(UIheight,UIwidth)
    tab2.text = "Monthly View"
    
    val tab3 = new WeeklyView(UIheight,UIwidth)
    tab3.text = "Weekly View"

    val tab4 = new Statistics(UIheight,UIwidth)
    tab4.text = "Statistics"

    root.getTabs.add(tab1)
    root.getTabs.add(tab2)
    root.getTabs.add(tab3)
    root.getTabs.add(tab4)
    root.tabClosingPolicy = TabClosingPolicy.Unavailable

    // Calendar Pane
//    val calendarPane = new CalendarView()

    val scene = Scene(parent = root)
    stage.scene = scene

  end start

end Main

