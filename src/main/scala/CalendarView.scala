import scalafx.scene.control.{Button, Label, Tab, TabPane}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.Scene
import scalafx.scene.layout.{FlowPane, GridPane, HBox, StackPane, VBox, Border, BorderStroke, Background}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.Stage

import scala.collection.mutable
import java.io.IOException
import java.time.*
import java.util.*
//
class MonthlyView(parentHeight: Double, parentWidth:Double) extends Tab {
  private val ownWidth = parentWidth
  private val ownHeight = parentHeight

  private var entries = Diary.diary

  val calendar = new GridPane {
    prefWidth = parentWidth * 0.8
    prefHeight = parentHeight * 0.9
  }
  calendar.alignment = Pos.Center
  calendar.hgap = 15
  calendar.vgap = 15

  var dateSelected: LocalDateTime = LocalDateTime.now()
  val today = LocalDateTime.now()

  val nextMonth_button = new Button("Next Month"):
    onMouseClicked = (event) => {
      dateSelected = dateSelected.plusMonths(1)
      val title = monthyearLabel(dateSelected)
      calendar.children.clear()
      MonthlyView.this.content = wholeCalendar(dateSelected: LocalDateTime)
    }

  val lastMonth_button = new Button("Previous Month"):
    onMouseClicked = (event) => {
      dateSelected = dateSelected.minusMonths(1)
      val title = monthyearLabel(dateSelected)
      calendar.children.clear()
      MonthlyView.this.content = wholeCalendar(dateSelected: LocalDateTime)
    }

  val monthButtons = new HBox:
    spacing = 15
    children = Array(lastMonth_button, nextMonth_button)

  //the whole UI + controls
  def wholeCalendar(datefocus: LocalDateTime): VBox =
    var whole = new VBox:
      spacing = 15
      val label = monthyearLabel(datefocus)
      this.padding = Insets(10,10,10,10)
      children = Array(label,drawCalendar(datefocus))
    whole

  this.content = wholeCalendar(dateSelected: LocalDateTime)

  def monthyearLabel(datefocus: LocalDateTime): HBox =
    var t = new HBox:
      spacing = 400
      var l = new Label()
      l.text = (datefocus.getMonth.name() + " " + datefocus.getYear.toString)
      l.font = new Font(18)
      children = Array(monthButtons,l)
    t

  //draw calendar
  def drawCalendar(datefocus: LocalDateTime): GridPane =

    val calendarWidth = calendar.prefWidth.toDouble
    val calendarHeight = calendar.prefHeight.toDouble
    val spacingH = 5
    val spacingV = 5

    //calendar algorithm to get the correct days
    val day = today.getDayOfMonth
    //leap year check
    var leapyear = true
    if (datefocus.getYear) % 4 != 0 then
      leapyear = false
    var monthLength = datefocus.getMonth.length(leapyear)

    // get first day of month
    val firstDayofMonth = LocalDate.of(datefocus.getYear,datefocus.getMonthValue,1)

    val offset = firstDayofMonth.getDayOfWeek.getValue - 1
    // draw the calendar
    for i <- 0 to 6
        j <- 0 to 6
    do
      if (i == 0) then
        val dayName = j match
          case 0 => "Mon"
          case 1 => "Tue"
          case 2 => "Wed"
          case 3 => "Thurs"
          case 4 => "Fri"
          case 5 => "Sat"
          case 6 => "Sun"
        val label = new Label(dayName)
        label.alignmentInParent = Pos.Center
        calendar.add(label, j, i)
      else
        var stackPane = new StackPane()
        val day_square = new Rectangle()
        day_square.fill = Color.rgb(241,222,238)
        day_square.stroke = Color.rgb(230,166,247)
        day_square.strokeWidth = 1
        day_square.width = 150
        day_square.height = 100
        stackPane.children += day_square
        
        //calculate the date
        var date = (j+1)+(7*i) - 7
        if date > offset then
          val currentdate = date - offset
          if currentdate <= monthLength then
            var dateStr = new Text(currentdate.toString)
            var m = ""
            var da = ""
            if datefocus.getMonth.getValue < 10 then m = "0" + datefocus.getMonth.getValue.toString
            else m = datefocus.getMonth.getValue.toString
            if currentdate < 10 then da = s"0${currentdate.toString}"
            else da = currentdate.toString
            val parse = s"${datefocus.getYear.toString}-$m-$da"
            if LocalDate.parse(parse).isEqual(LocalDate.now()) then
              dateStr.stroke = Color.Red
              dateStr.text = currentdate.toString + " (today)"
              
            var entry = entries.filter(e => e.date == parse)
            val entryBox = new VBox:
              var arr = entry.map(e => (Text("Entry ID: " + e.id.toString + ", time: " + e.timeSpent.toString + " mins" ))).toArray
              spacing = 4
              children = arr
              alignment = Pos.Center
            val box = new VBox:
              spacing = 6
              children = Array(dateStr, entryBox)
              alignment = Pos.TopCenter
              alignmentInParent = Pos.TopCenter
              
            stackPane.children += box
            stackPane.alignment = Pos.TopCenter
        calendar.add(stackPane,j,i)

    calendar

}
class WeeklyView(parentHeight: Double, parentWidth:Double) extends Tab {
  private val ownWidth = parentWidth
  private val ownHeight = parentHeight
  private var entries = Diary.diary

  var datefocus: LocalDateTime = LocalDateTime.now()
  val today = LocalDateTime.now()

  val calendar = new GridPane {
    prefWidth = parentWidth * 0.8
    prefHeight = parentHeight * 0.9
  }
  calendar.alignment = Pos.Center
  calendar.hgap = 15
  calendar.vgap = 15

  val nextWeek_button = new Button("Next Week"):
    onMouseClicked = (event) => {
      datefocus = datefocus.plusWeeks(1)
      calendar.children.clear()
      Calendar(datefocus)
    }

  val lastWeek_button = new Button("Previous Week"):
    onMouseClicked = (event) => {
      datefocus = datefocus.minusWeeks(1)
      calendar.children.clear()
      Calendar(datefocus)
    }

  val weekButtons = new HBox:
    this.padding = Insets(10,10,10,10)
    spacing = 15
    children = Array(lastWeek_button, nextWeek_button)

  val wholeCalendar = new VBox:
    spacing = 15
    children = Array(weekButtons, Calendar(datefocus))

  this.content = wholeCalendar

  def Calendar(datefocus: LocalDateTime): GridPane =
    val calendarWidth = calendar.prefWidth.toDouble
    val calendarHeight = calendar.prefHeight.toDouble
    val spacingH = 5
    val spacingV = 5

    //calendar algorithm to get the correct days
    val dayfocusoftheweek = datefocus.getDayOfWeek.getValue - 1
    // get first day of week
    var firstDayofWeek = datefocus.minusDays(dayfocusoftheweek)
    
    //save the date text for entries purpose
    var dates = mutable.Buffer[String]()

    // draw the calendar
    for i <- 0 to 2
        j <- 0 to 6
    do
      if (i == 0) then
        val dayName = j match
          case 0 => "Mon"
          case 1 => "Tue"
          case 2 => "Wed"
          case 3 => "Thurs"
          case 4 => "Fri"
          case 5 => "Sat"
          case 6 => "Sun"
        val label = new Label(dayName)
        label.alignmentInParent = Pos.Center
        calendar.add(label, j, i)
      else if (i == 1) then
        //calculate date
        val day = firstDayofWeek.plusDays(j)
        val dayValue = day.getDayOfMonth.toString
        val monthValue = day.getMonthValue.toString
        val daytitle = Text(dayValue + "/" + monthValue)

        var m = ""
        var d = dayValue
        if day.getMonth.getValue < 10 then m = "0" + day.getMonth.getValue.toString
        if day.getDayOfMonth < 10 then d = s"0${dayValue}"
        val parse = s"${datefocus.getYear.toString}-$m-$d"
        if LocalDate.parse(parse).isEqual(LocalDate.now()) then
          daytitle.stroke = Color.Red
          daytitle.text = dayValue + "/" + monthValue + " (today)"
        
        daytitle.alignmentInParent = Pos.Center
        dates += parse
        calendar.add(daytitle,j,i)
      else
        val rec = new VBox:
          prefWidth = 150
          prefHeight = 500
          border = Border.stroke(Color.rgb(255,164,18))
          background = Background.fill(Color.rgb(247,227,153))
          spacing = 5
          alignment = Pos.TopCenter
          
          val current = dates(j)
          var entry = entries.filter(e => e.date == current)
          var arr = entry.map(e => (Text("ID: " + e.id.toString + ", time: " + e.timeSpent.toString + " mins"))).toArray
          children = arr
        
        calendar.add(rec, j, i)

    calendar


//  val getMonthEntries = collection.mutable.Map[Integer, List[Entry]]()
}
