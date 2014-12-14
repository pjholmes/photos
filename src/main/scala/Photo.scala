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

import grizzled.slf4j.Logger
import java.util.Date
import java.security.MessageDigest
import java.nio.file.{Files, Paths}

object Photo {
  val logger = Logger("Photo")
}

class Photo (val name: String, val size: Long) {

  lazy val date : Option[Date] = ImageExif.getImageDate(name)

  lazy val digest : String = {
    val content = Files.readAllBytes(Paths.get(name))
    MessageDigest.getInstance("MD5").digest(content).map("%02x".format(_)).mkString
  }

  override def toString() = {
    val dateStr : String = date match {
      case Some(v) => v.toString
      case None => "None"
    }
    s"$name size: $size  date: $dateStr digest: $digest"
  }
}

