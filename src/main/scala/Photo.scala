/*
Copyright 2014 Patrick Holmes

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.pjholmes.photos

import java.io.File
import grizzled.slf4j.Logger
import java.security.MessageDigest
import java.nio.file.{Files, Paths}
import org.joda.time.LocalDateTime

/**
 * Represents a photo file.
 * @param fileName The file name of the photo on disk
 * @param fileSize The file size on disk
 * @param fileCreateDateTime The file creation time
 */
class Photo (val fileName: String, val fileSize: Long, val fileCreateDateTime: LocalDateTime) extends Ordered[Photo] {

  lazy val exifDateTime : Option[LocalDateTime] = PhotoExif.getPhotoDate(fileName)

  lazy val fileDigest : String = {
    val content = Files.readAllBytes(Paths.get(fileName))
    MessageDigest.getInstance("MD5").digest(content).map("%02x".format(_)).mkString
  }

  // this makes both values available, regardless of access order, while
  // doing the calculation only once
  lazy val bestCaptureDateTimeAndScore = getBestCaptureDateTimeAndScore()
  lazy val bestCaptureDateTime = bestCaptureDateTimeAndScore._1
  lazy val score = bestCaptureDateTimeAndScore._2

  val extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase

  /** Return the best date representing when the image was captured and a date quality score
    * Use exif create date if available.
    * If not, use a regex to find the date and time in the file name itself.
    * Last resort, use the file creation dateTime.
    */
  def getBestCaptureDateTimeAndScore() : (LocalDateTime, Int) = {
    if (exifDateTime.isDefined) {
      (exifDateTime.get, 3)
    } else {
      val name = new File(fileName).getName // without path
      name match {
        case Photo.dateRegex(y, m, d, h, mm, s) =>
          val dt = new LocalDateTime(y.toInt, m.toInt, d.toInt, h.toInt, mm.toInt, s.toInt)
          (dt, 2)
        case _ =>
          (fileCreateDateTime, 1)
      }
    }
  }

  override def toString() = {
    val exifStr : String = exifDateTime match {
      case Some(v) => v.toString
      case None => "None"
    }
    s"$fileName fileSize: $fileSize create: $fileCreateDateTime exif: $exifStr digest: $fileDigest"
  }

  /**
   * Compare this photo with that photo such that photos will be sorted by descending score and ascending datetime
   * @param that
   * @return
   */
  override def compare(that: Photo): Int = {
    val a = that.score - this.score // descending
    if (a == 0)
      return this.bestCaptureDateTime.compareTo(that.bestCaptureDateTime)
    else
      return a
  }
}

object Photo {
  val logger = Logger("Photo")
  val dateRegex = """.*(\d\d\d\d)[-_](\d\d)[-_](\d\d)[-_ ](\d\d)[-_.](\d\d)[-_.](\d\d).*""".r
}

