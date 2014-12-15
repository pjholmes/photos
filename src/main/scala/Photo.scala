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
import java.util.Date
import java.security.MessageDigest
import java.nio.file.{Files, Paths}

object Photo {
  val logger = Logger("Photo")
  val format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
  val reg = """.*(\d\d\d\d)[-_](\d\d)[-_](\d\d)[-_ ](\d\d)[-_.](\d\d)[-_.](\d\d).*""".r
}

class Photo (val origName: String, val origSize: Long, val origCreateDate: Date) {

  lazy val exifDate : Option[Date] = ImageExif.getImageDate(origName)

  lazy val origDigest : String = {
    val content = Files.readAllBytes(Paths.get(origName))
    MessageDigest.getInstance("MD5").digest(content).map("%02x".format(_)).mkString
  }

  /** The date to be used to create the file name.
    * Used exif create date if available.
    * If not, use a regex to find the date and time in the file name.
    * If that fails, use the file creation time.
    */
  lazy val targetDate : Date = {
    if (exifDate.isDefined) {
      exifDate.get
    } else {
      val name = new File(origName).getName
      name match {
        case Photo.reg(y, m, d, h, mm, s) =>
          val dt = new Date(y.toInt - 1900, m.toInt - 1, d.toInt, h.toInt, mm.toInt, s.toInt)
          dt
        case _ =>
          origCreateDate
      }
    }
  }

  lazy val targetName : String = {
    Photo.format.format(targetDate) + origName.substring(origName.lastIndexOf('.')).toLowerCase;
  }

  override def toString() = {
    val dateStr : String = exifDate match {
      case Some(v) => v.toString
      case None => "None"
    }
    s"$origName origSize: $origSize  origDate: $dateStr targetName: $targetName" // origDigest: $origDigest"
  }
}

