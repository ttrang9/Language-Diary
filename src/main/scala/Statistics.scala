import scalafx.scene.control.{Button, DatePicker, Label, Tab, TabPane}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.Scene
import scalafx.scene.layout.{FlowPane, GridPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage
import scalafx.scene.chart.{CategoryAxis, LineChart, NumberAxis, XYChart}
import scalafx.collections.ObservableBuffer

import scala.collection.mutable.Map
import scalafx.Includes.*

import java.io.IOException
import java.time.*
import java.util.*
import Main.stage

class Statistics(parentWidth: Double, parentHeight: Double) extends Tab:
  private val ownWidth = parentWidth
  private val ownHeight = parentHeight
  
  //Chart for amount of time learned by day
  var entries = new EntryInput(ownWidth,ownHeight).entries
  //var entries:ObservableBuffer[Entry] = Diary.diary.to(ObservableBuffer)
  var res: Map[String, Int] = collection.mutable.Map()
  for e <- entries do
    if res.contains(e.date) then
      res(e.date) = res(e.date) + e.timeSpent
    else
      res(e.date) = e.timeSpent

  val s = res.toSeq.sortBy(((d, t) => d))

  val xyData = s

  // Prepare series
  val series = new XYChart.Series[String, Number] {
    name = "Time Spent"
    data() ++= xyData.map {
      case (x, y) => XYChart.Data[String, Number](x, y)
    }
  }
  // setup Line chart
  val c = new LineChart[String,Number](CategoryAxis("Date"), NumberAxis("Time Spent")) {
    title = "Time Spent Learning Language By Day"
    data() += series
  }

  //Chart for number of vocab learned by day
  var res2: Map[String, Int] = collection.mutable.Map()
  for e <- entries do
    if res2.contains(e.date) then
      res2(e.date) = res2(e.date) + e.vocab.length
    else
      res2(e.date) = e.vocab.length

  val vocab = res2.toSeq.sortBy(((d, v) => d))

  val xyData2 = vocab

  // Prepare series
  val series2 = new XYChart.Series[String, Number] {
    name = "Vocab learned"
    data() ++= xyData2.map {
      case (x, y) => XYChart.Data[String, Number](x, y)
    }
  }
  // setup Line chart
  val b = new LineChart[String, Number](CategoryAxis("Date"), NumberAxis("Number of vocabs learned")) {
    title = "Number of Vocabulary Learned by Day"
    data() += series2
  }

  val label = new Label("Some Statistics")
  label.font = Font.font(18)

  val grid = new GridPane() {
    this.add(label,2,1,1,1)
    this.add(c, 1, 2,1,1)
    this.add(b, 3, 2, 1, 1)
  }
  
  // Some more summary statistics
  val label2 = new Label("Summary"):
    alignmentInParent = Pos.Center
    margin = Insets(10,10,10,10)
  label2.font = Font.font(18)
  grid.add(label2, 1, 5,1,1)
  
  //Average time spent each day;
  val avgT = (s.map(((d, t) => t)).sum * 1.0) / (1.0 * s.size)
  val avgTime = new Label(f"Average time spent each day: $avgT%2.2f minutes"):
    margin = Insets(10,10,10,10)
    alignmentInParent = Pos.Center
  grid.add(avgTime,1,6,1,1)  
  
  //Average vocab learned each day  
  val avgW = ((vocab.map(((d, w) => w))).sum * 1.0) / (1.0 * vocab.size)
  val avgWords = new Label(f"Average words learned each day: $avgW%2.2f words"):
    margin = Insets(10,10,10,10)
    alignmentInParent = Pos.Center
  grid.add(avgWords,1,7,1,1)
  

  this.content = grid
