import java.io.{BufferedReader, BufferedWriter, FileNotFoundException, FileReader, FileWriter, IOException, PrintWriter}
import scala.collection.mutable.{ArrayBuffer, ArraySeq, Map}
import scala.io.Source

// I/O
object VocabularyIO:
//  private var allNouns = ArrayBuffer.empty[String]
//  private var allVerbs = ArrayBuffer.empty[String]
//  private var allAdjs = ArrayBuffer.empty[String]
  private var allWords = Map[String, String]()

//  def nouns: Array[String] = allNouns.toArray
//
//  def verbs: Array[String] = allVerbs.toArray
//
//  def adjs: Array[String] = allAdjs.toArray

  def words: Map[String,String] = allWords

  def size = allWords.size

  def readFile(sourceFile: String): Array[String] =
    val myFileReader =
      try FileReader(sourceFile)
      catch
        case e: FileNotFoundException =>
          println("File not found")
          return Array[String]()

    val lineReader = BufferedReader(myFileReader)
    try
      wordProcess1(lineReader)
    catch
      case e: IOException =>
        println("Reading finished with error")
        return Array[String]()

  end readFile
  
  def wordProcess1(lineReader: BufferedReader): Array[String] =
    var today = Array[String]()
    var oneLine: String = lineReader.readLine()
    
    while oneLine != null do
      oneLine = oneLine.trim.toLowerCase()
      println(oneLine)
      oneLine match
        case word if (word.contains(":")) => today = today :+ oneLine.split(':')(0).toLowerCase()
        case empty if (empty.trim.isEmpty()) =>
        case _ => 

      oneLine = lineReader.readLine()    
      
    today

class CorruptedVocabFileException(message: String) extends Exception(message)