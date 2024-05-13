import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.beans.binding.Bindings

import scala.collection.mutable.ListBuffer
import upickle.default.{macroRW, ReadWriter as RW}
import upickle.default.ReadWriter.join
import io.circe.*
import io.circe.generic.auto.*
import cats.syntax.either.*

import java.time.*
import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Random, Success, Try, Using}
import java.nio.file.{Files, Paths}
import java.nio.charset.Charset

class Diary():
  // Initialize everything
  var entries = Diary.diary
  
  
object Diary:
  var diary = this.getEntries("diary.json")

  def getEntries(filename: String): List[Entry] =
  //Exception-handled file reading with Using / Try
    Using(Source.fromResource(s"${filename}")) { source =>
      upickle.default.read[SaveFormat](source.mkString)
    } match
      case Success(save) => save.data
      case Failure(e) =>
        println("Error loading data:\n" + e)
        List()

  //methods for saving/editing/deleting file
  def editEntry(entry: Entry): Unit =
    val i = diary.indexWhere(_.id == entry.id)
    diary = diary.updated(i, entry)
    val updatedData = SaveFormat(diary)
    Using(
      Files.newBufferedWriter(
        Paths.get("src/main/resources/diary.json"),
        Charset.forName("UTF-8")
      )
    ) { writer =>
      upickle.default.writeTo[SaveFormat](updatedData, writer, 2)
    } match
      case Failure(e) => println("Error deleting entry data:\n" + e)
      case Success(value) =>

  def deleteEntry(entry: Entry): Unit =
    diary = diary.filter(_.id != entry.id)
    val updatedData = SaveFormat(diary)
    Using(
      Files.newBufferedWriter(
        Paths.get("src/main/resources/diary.json"),
        Charset.forName("UTF-8")
      )
    ) { writer =>
      upickle.default.writeTo[SaveFormat](updatedData, writer, 2)
    } match
      case Failure(e) => println("Error deleting entry data:\n" + e)
      case Success(value) =>

  def saveNewEntry(entry: Entry): Unit =
    diary = diary.appended(entry)
    val updatedData = SaveFormat(diary)
    //Exception-handled file writing with Using / Try
    Using(
      Files.newBufferedWriter(
          Paths.get("src/main/resources/diary.json"),
          Charset.forName("UTF-8")
        )
    ) { writer =>
      upickle.default.writeTo[SaveFormat](updatedData, writer, 2)
    } match
      case Failure(e) => println("Error writing entry data:\n" + e)
      case Success(value) =>

  //Case classes for reading/writing json files - saving/deleting files
case class Entry(id: Int, date:String, buddy:String, comment:String, 
                 skills:SkillList, vocab:Array[String], timeSpent:Int):
  val IDProperty = ObjectProperty[Int](id)
  val dateProperty = StringProperty(date)
  val buddyProperty = StringProperty(buddy)
  val commentProperty = StringProperty(comment)
  val skillsProperty = skills.skills.map(ObjectProperty(_)).to(ObservableBuffer)
  //val skillsListProperty = skills.to(ObservableBuffer)
  val vocabProperty = vocab.map(StringProperty(_)).to(ObservableBuffer)
  val timeSpentProperty = ObjectProperty[Int](timeSpent)

object Entry:
  implicit val rw: RW[Entry] = macroRW
//
case class Skill(name: String, check: Boolean):
  val nameProperty = StringProperty(name)
  val checkProperty = BooleanProperty(check)

object Skill:
  implicit val rw: RW[Skill] = macroRW

case class SkillList(val skills: List[Skill])
object SkillList:
  implicit val rw: RW[SkillList] = macroRW

case class SaveFormat(val data: List[Entry]):
  val diaryProperty = ObservableBuffer().appendAll(data)
object SaveFormat:
  implicit val rw: RW[SaveFormat] = macroRW

