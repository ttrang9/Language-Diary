import Main.stage
import javafx.beans.value.{ChangeListener, ObservableStringValue}
import scalafx.Includes.*
import scalafx.beans.binding.Bindings
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.{Background, BackgroundFill, Border, CornerRadii, GridPane, HBox, VBox}
import scalafx.scene.text.Font
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.scene.control.DatePicker
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.scene.paint.Color.Black
import scalafx.collections.ObservableBuffer
import scalafx.collections.transformation.FilteredBuffer
import scalafx.scene.paint.Color
import scalafx.scene.input.KeyEvent

import java.time.*
import java.time.format.DateTimeParseException
import java.io.*
import scala.collection.mutable

// GUI
class EntryInput(parentWidth: Double, parentHeight: Double) extends Tab:
  private val ownWidth: Double = parentWidth * 0.6
  private val ownHeight: Double = parentHeight

  var entries = Diary.diary.to(ObservableBuffer)
  //private var entries = Diary.diary
  private val skillsArray = Array("Speaking","Listening","Reading","Writing")

  val spacingS = 15

  val newEntry = new Label("Entry"):
    font = new Font(18)

  var ID = new Label()
  var datePicked:ObjectProperty[LocalDate] = ObjectProperty(LocalDate.now())
  
  //date
  val datePicker = new DatePicker()
  datePicker.onKeyTyped = (e) => {
    val dateValue = datePicker.value.value
    try LocalDate.parse(dateValue.toString)
    catch
      case e:DateTimeParseException =>
        val alert = new WrongDateFormat
        alert.showAndWait()
    datePicked = datePicker.value
    println("Date: " + dateValue)
  }
  
  //vocabulary text file
  val file_status = new Label("")
  var file_status_check = false
  var vocab = Array[String]()

  val vocab_button = new Button("Load vocab file"):
    onMouseClicked = (event) => {
      val chooser = new FileChooser:
        title = "New vocab file"
        //users can only choose text file
        extensionFilters.add(ExtensionFilter("Text Files", "*.txt"))
      val selectedFile = chooser.showOpenDialog(stage)
      if (chooser!=null) {
        println("Open: " + selectedFile)
        file_status.text = "Open: " + selectedFile.getName
        file_status_check = true
        vocab = Array[String]()
        vocab = vocab :++ VocabularyIO.readFile(selectedFile.getPath)
        println(VocabularyIO.readFile(selectedFile.getPath).mkString("Array(", ", ", ")"))
        println(vocab.mkString("Array(", ", ", ")"))
      }
      else {
        file_status.text = "No file choosen"
      }
    }

  //person involved with learning
  val buddy = new ChoiceBox[String]:
    items = ObservableBuffer("None :(", "Teacher", "Friend :)")
    selectionModel().selectFirst()

  buddy.prefWidth = 75
  buddy.prefHeight = 5

  val comments = new TextArea():
    promptText = "write some comment abt your experience here"
    prefHeight = 150
    prefWidth = 300

  var totalTimeSpent = new TextField()
  var testScore = new TextField()

  //buttons
  val delete = new Button("Delete Entry"):
    onAction = (event) => { deleteEntry() }
  val edit = new Button("Edit Entry"):
    onAction = (event) => editEntry()
  val submit = new Button("Add Entry"):
    onAction = (event) => {
      addEntry()
    }
  val clear = new Button("Clear All Fields"):
    onAction = (event) => clearFields()

  //UI
  val titleBox = new HBox:
    prefWidth = ownWidth / 3
    alignment = Pos.Center
    children = Array(newEntry)
  titleBox.alignment = Pos.Center

  val dateInput = new HBox:
    spacing = spacingS
    children = Array(Label("Date"), datePicker)

  val vocabInput = new HBox:
    spacing = spacingS
    children = Array(Label("Vocabulary"), vocab_button, file_status)

  val buddyInput = new HBox:
    spacing = spacingS
    children = Array(Label("Study Buddy"), buddy)

  val commentInput = new HBox:
    spacing = spacingS
    children = Array(Label("Comments"), comments)

  val totalTimeInput = new HBox:
    spacing = spacingS
    children = Array(Label("Total Time Spent (in minutes): "),totalTimeSpent)
  
  val entryForm = new VBox:
    prefWidth = ownWidth * 0.4
    spacing = spacingS

  val submitForm = new HBox:
    spacing = spacingS
    background = Background.fill(Color.rgb(220, 199, 240))
    alignment = Pos.BottomRight
    padding = Insets(10, 10, 10, 10)
    children = Array(clear, edit, delete, submit)

  val entryTab = new VBox:
    //style the whole entry input vbox + add childrenn
    background = Background.fill(Color.rgb(220, 199, 240))
    spacing = spacingS
//    border = Border.stroke(Black)
    padding = Insets(10, 10, 10, 20)
    margin = Insets(10, 0, 0, 0)
    children = Array(titleBox, dateInput, vocabInput, buddyInput, commentInput, totalTimeInput)

  private var speaking = skillInput("Speaking")
  private var listening = skillInput("Listening")
  private var reading = skillInput("Reading")
  private var writing = skillInput("Writing")
  //skills gridpane
  val allSkills = new HBox:
    spacing = 5
    padding = Insets(10,0,0,0)
    val slabel = new Label("Skills learned"):
      margin = Insets(0,0,0,20)
    var skills = ObservableBuffer[skillInput](speaking,listening,reading,writing)
    children = Array(slabel, speaking,listening,reading,writing)

  val skillcontent = VBox(entryTab, allSkills, submitForm)
  allSkills.background = Background.fill(Color.rgb(220, 199, 240))

  val list = new Label("List of Entries"):
    font = new Font(18)
    
  //Entry table for looking up and choosing entries
  private var tableTest = entries

  val entryTable = new TableView[Entry](tableTest) {
    columns ++= Seq(
      new TableColumn[Entry, Int] {
        text = "ID"
        cellValueFactory = _.value.IDProperty
      },
      new TableColumn[Entry, String]() {
        text = "Date"
        cellValueFactory = _.value.dateProperty
      },
      new TableColumn[Entry, Int]() {
        text = "Total time spent"
        cellValueFactory = _.value.timeSpentProperty
      },
      new TableColumn[Entry, String]() {
        text = "Study Buddy"
        cellValueFactory = _.value.buddyProperty
      },
      new TableColumn[Entry, Int]() {
        text = "Number of vocabs"
        cellValueFactory = en => ObjectProperty[Int](en.value.vocabProperty.length)
      },
      new TableColumn[Entry, String]() {
        text = "Comment"
        cellValueFactory = _.value.commentProperty
      },
      new TableColumn[Entry, Boolean]() {
        text = "S"
        cellValueFactory = en => ObjectProperty[Boolean](en.value.skillsProperty(0).value.check)
      },
      new TableColumn[Entry, Boolean]() {
        text = "L"
        cellValueFactory = en => ObjectProperty[Boolean](en.value.skillsProperty(1).value.check)
      },
      new TableColumn[Entry, Boolean]() {
        text = "R"
        cellValueFactory = en => ObjectProperty[Boolean](en.value.skillsProperty(2).value.check)
      },
      new TableColumn[Entry, Boolean]() {
        text = "W"
        cellValueFactory = en => ObjectProperty[Boolean](en.value.skillsProperty(3).value.check)
      }
    )
    placeholder = new Label("No entries to display")
  }

  entryTable.onMouseClicked = (event) => {
    if entryTable.selectionModel.value.selectedItemProperty().value != null then
      val entryPicked = entryTable.selectionModel.value.selectedItemProperty().value
      setFields(entryPicked)
    else clearFields()
  }

  entryTable.selectionModel.value.selectedIndexProperty().onChange {
    val entryIndex = entryTable.selectionModel.value.selectedIndex.value
    if (entryIndex >= 0) {
      setFields(entryTable.selectionModel.value.getSelectedItem)
    }
  }

  //get the EntryInput information
  var entryID = ObjectProperty[Int](0)
  if entries.isEmpty then entryID = ObjectProperty[Int](0)
  else entryID = ObjectProperty[Int](entries.maxBy(_.id).id + 1)
  entries.onChange((_, changes) =>
    for changeEvent <- changes do
      changeEvent match
        case ObservableBuffer.Update(from,to) => entryID = entryID
        case ObservableBuffer.Remove(_,removed) =>
        case ObservableBuffer.Add(_,added) =>
          entryID = ObjectProperty[Int](entries.maxBy(_.id).id + 1)
        case _ =>
  )

  var cmt = StringProperty(comments.text.value).value
  comments.text.onChange {
    cmt = StringProperty(comments.text.value).value
  }
  var stubud = StringProperty(buddy.selectionModel().getSelectedItem)
  buddy.value.onChange {
    stubud = StringProperty(buddy.value.value)
  }
  
  var time = totalTimeSpent.text
  totalTimeSpent.text.onChange {
    time = StringProperty(totalTimeSpent.text.value)
    var timeSpent = time match
      case beginning if time.value == "" => 0
      case number if time.value.toIntOption.isDefined => number.value.toInt
      case e if time.value.toIntOption.isEmpty =>
        new Alert(AlertType.Warning, "Please write a number or leave blank").showAndWait()
  }

  val entriesCopy: ObservableBuffer[Entry] = ObservableBuffer()

  //filter entries by day/interval
  val intervalLabel = new Label("Filter entries by time"):
    padding = Insets(10,10,10,20)
    alignment = Pos.BaselineCenter

  var from = new DatePicker()
  from.onAction = (e) => {
    val dateValue = from.value.value
    println("Date: " + dateValue)
  }
  from.margin = Insets(10,10,10,0)
  var to = new DatePicker()
  to.onAction = (e) => {
    val dateValue = to.value.value
    println("Date: " + dateValue)
  }
  to.margin = Insets(10,10,10,20)
  
  //filter entries by study buddy
  val buddyFilterLabel = new Label("Filter entries by study buddy"):
    padding = Insets(10,10,5,20)
  val buddyFilter = new ChoiceBox[String]:
    items = ObservableBuffer("No selection","None :(", "Teacher", "Friend :)")
    selectionModel().selectFirst()
  buddyFilter.prefWidth = 100
  buddyFilter.prefHeight = 5
    
  val skillsFilterLabel = new Label("Filter entries by skill learned"):
    padding = Insets(10,10,5,20)
  val skillsFilter = new ChoiceBox[String]:
    items = ObservableBuffer("No selection","Speaking","Listening","Reading","Writing")
    selectionModel().selectFirst()
    
  val filterGrid = new GridPane():
    this.add(intervalLabel,1,1, 1,1)
    this.add(from,2,1,1,1 )
    this.add(to,3, 1, 1, 1)
    this.add(buddyFilterLabel,1, 2, 1, 1)
    this.add(buddyFilter,2, 2, 1, 1)
    this.add(skillsFilterLabel, 1, 3, 1, 1)
    this.add(skillsFilter, 2, 3, 1, 1)
    

  val entriesFilterGet = new Button("Apply"):
    onMouseClicked = (event) => {
      var fromPicked = LocalDate.MIN
      from.value.value match
        case e:LocalDate =>
          fromPicked = from.value.value
          filterFirst(fromPicked)
        case _ =>
          val alert= new WrongDateFormat
          alert.showAndWait()
    }
    
  //Helper methods for filtering entries  
  private def filterFirst(fromPicked1:LocalDate): Unit =
    var toPicked = LocalDate.MAX
    to.value.value match
      case e: LocalDate =>
        toPicked = to.value.value
        filter(fromPicked1, to.value.value)
      case _ =>
        val alert = new WrongDateFormat
        alert.showAndWait()
  private def filter(fromPicked:LocalDate, toPicked: LocalDate): Unit =
    var entriesChanges = entries.filter(e => ((LocalDate.parse(e.date).isAfter(fromPicked)
      && LocalDate.parse(e.date).isBefore(toPicked))
      || LocalDate.parse(e.date).isEqual(fromPicked) || LocalDate.parse(e.date).isEqual(toPicked)))
    val budFilter = buddyFilter.selectionModel().selectedItemProperty().get()
    if budFilter != "No selection" then
      entriesChanges = entriesChanges.filter(e => e.buddy == budFilter)
      
    val skillFilter = skillsFilter.selectionModel().selectedItemProperty().get()  
    if skillFilter != "No selection" then
      skillFilter match
        case "Speaking" => entriesChanges = entriesChanges.filter(e => e.skills.skills.head.check)
        case "Listening" => entriesChanges = entriesChanges.filter(e => e.skills.skills(1).check)
        case "Reading" => entriesChanges = entriesChanges.filter(e => e.skills.skills(2).check)
        case "Writing" => entriesChanges = entriesChanges.filter(e => e.skills.skills(3).check)
      
    entryTable.items = entriesChanges
  
  val resetButton = new Button("Reset"):
    alignmentInParent = Pos.BottomRight
  resetButton.onMouseClicked = (event) => {
    entryTable.items = entries
    val entriesSort = entries.sortBy(e => e.date)
    from.value.value = LocalDate.parse(entriesSort.head.date)
    to.value.value = LocalDate.parse(entriesSort.last.date)
    skillsFilter.selectionModel().selectFirst()
    buddyFilter.selectionModel().selectFirst()
  }

  val filterButton = new HBox:
    margin = Insets(10, 5, 10, 20)
    spacing = 10
    children = Array(entriesFilterGet, resetButton)

  val splitPaneRight = new SplitPane
  splitPaneRight.orientation = scalafx.geometry.Orientation.Vertical
  splitPaneRight.items += entryTable  

  val filterArea = new VBox:
    children = Array(filterGrid,filterButton)
  splitPaneRight.items += filterArea

  //Text area for displaying vocabulary when choosing entries from the Entry Table
  val textArea = new TextArea():
    margin = Insets(10,10,10,5)
  val grid = new GridPane():
    this.add(skillcontent,1,1,1,1)
    val label = new Label("Vocabulary List "):
      margin = Insets(10,10,10,10)
    this.add(label,1,1,1,1)
    this.add(textArea,2,1,2,2)
    
  //UI  
  val splitPaneLeft = new SplitPane:
    background = Background.fill(Color.rgb(220, 199, 240))
  splitPaneLeft.orientation = scalafx.geometry.Orientation.Vertical
  splitPaneLeft.items += skillcontent
  splitPaneLeft.items += grid
  splitPaneLeft.dividerPositions_=(0.55, 0.45)
  splitPaneRight.dividerPositions_=(0.55, 0.45)

  val splitPane = new SplitPane
  splitPane.items += splitPaneLeft
  splitPane.items += splitPaneRight
  splitPane.dividerPositions_=(0.40, 0.60)
  
  this.content = splitPane

  //Helper methods for the UI to work

  private def deleteEntry() =
    if entryTable.selectionModel.value.selectedIndexProperty().value == -1 then
      val alert = new MissingEntry
      alert.showAndWait()
    else
      val entryPicked = entryTable.selectionModel.value.selectedIndexProperty().value
      val i = entries(entryPicked)
      entries.remove(i)
      Diary.deleteEntry(i)
      entryTable.selectionModel().clearSelection()
      
  private def editEntry() =
    if entryTable.selectionModel.value.selectedIndexProperty().value == -1 then
      new Alert(AlertType.Warning, "You haven't chosen any entry to edit").showAndWait()

    if !(ValidDate(datePicker.value.value)) then
      val alert = new WrongDateFormat
      alert.showAndWait()

    if totalTimeSpent.text.value.toIntOption.isEmpty then
      new Alert(AlertType.Warning, "You have to type in a number for total time spent").showAndWait()

    var t: List[Skill] = List(Skill("speaking", speaking.check.value), Skill("listening", listening.check.value),
      Skill("reading", reading.check.value), Skill("writing", writing.check.value))
    val i = entryTable.selectionModel.value.selectedIndexProperty().value
    val entryPickedID = entries(i).id
    val entryPicked = Entry(entryPickedID, datePicker.value.value.toString, stubud.value, cmt,
                            SkillList(t), vocab, time.value.toInt)
    entries.update(i, entryPicked)
    Diary.editEntry(entryPicked)
    clearFields()
      
  private def addEntry() =
    if !ValidDate(datePicker.value.value) then
      val alert = new WrongDateFormat
      alert.showAndWait()
    validTime(totalTimeSpent.text.value)

    var t: List[Skill] = List(Skill("speaking", speaking.check.value), Skill("listening", listening.check.value),
                              Skill("reading", reading.check.value), Skill("writing", writing.check.value))
    val testEntry = Entry(entryID.value, datePicked.value.toString, stubud.value, cmt,
                          SkillList(t), vocab, totalTimeSpent.text.value.toInt)
    entries.add(testEntry)
    Diary.saveNewEntry(testEntry)
    clearFields()

  private def clearFields() =
    datePicker.value = LocalDate.now()
    comments.clear()
    buddy.selectionModel.value.selectFirst()
    totalTimeSpent.text = ""
    speaking.skillname.selected = false
    listening.skillname.selected = false
    reading.skillname.selected = false
    writing.skillname.selected = false
    textArea.clear()

  private def setFields(r: Entry) =
    datePicker.value = LocalDate.parse(r.date)
    comments.text = r.comment
    comments.text = r.comment
    buddy.selectionModel.value.select(r.buddy)
    totalTimeSpent.text = r.timeSpent.toString
    speaking.skillname.selected = r.skills.skills.head.check
    listening.skillname.selected = r.skills.skills(1).check
    reading.skillname.selected = r.skills.skills(2).check
    writing.skillname.selected = r.skills.skills(3).check
    textArea.text = r.vocab.mkString("\n")

  private def ValidDate(date: LocalDate): Boolean =
    try
      LocalDate.parse(date.toString)
      true
    catch
      case a: DateTimeParseException => false
      case b: NullPointerException => false
      case _: Throwable => false
 
  private def validTime(time: String): Unit =
    try time.toInt
    catch
      case _:Throwable =>
        new Alert(AlertType.Warning, "You have to type in a number for total time spent").showAndWait()

//Class for skill Input
class skillInput(name: String) extends HBox:
  prefHeight = 50
  prefWidth = 100
  margin = Insets(0,0,0,10)

  // choose to include the skill or not
  def nameAsString:String = name
  val skillname = CheckBox(name)
  var check = skillname.selected

  children = Array(skillname)

