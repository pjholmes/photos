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
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import java.util.Date

object ImageExif {
  def getImageDate(fileName: String) : Option[Date] = {
    try {
      val file = new File(fileName)
      val metadata = ImageMetadataReader.readMetadata(file)
      val directory = metadata.getDirectory(classOf[ExifSubIFDDirectory])
      if (directory ne null) {
        val date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
        return Some(date)
      }
      None
    } catch {
      case e: Exception => println(s"$fileName $e")
        return None
    }
  }
}
