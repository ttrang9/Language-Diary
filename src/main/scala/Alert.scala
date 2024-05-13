package scala

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class WrongDateFormat extends Alert(AlertType.Information) {
  this.setTitle("Wrong Date Format")
  this.setContentText("You should pick a date or write the date in the correct format")
}

class MissingEntry extends Alert(AlertType.Warning) {
  this.setTitle("Missing Entry")
  this.setContentText("You haven't chosen any entry to delete")
} 
