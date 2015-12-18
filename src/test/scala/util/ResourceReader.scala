package util

import scala.io.Source

object ResourceReader {
  def read(filename: String): String = {
    Source
      .fromInputStream(getClass.getResourceAsStream(filename))
      .mkString
  }
}
