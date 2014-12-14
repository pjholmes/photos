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

import ImageExif._
import grizzled.slf4j.Logger
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.util.Date

object Photos {

  val logger = Logger("Photos")

  val exts = List(".jpg", ".png", ".bmp")

  def isImageFile(fileName: String) = exts.contains(fileName.takeRight(4).toLowerCase)

  def main(args: Array[String]) {
    logger.info("Starting")
    val files = ArrayBuffer.empty[String]
    for (dir <- args)
      files ++= getImageFilesBelowDir(dir)
    for (file <- files) {
      val date : Option[Date] = getImageDate(file)
      date match {
        case Some(v) => println(s"$file: $v")
        case None => println(s"$file: <no date>")
      }
    }
    logger.info(s"Processed ${files.length} files")
  }

  def getImageFilesBelowDir(dir: String): ArrayBuffer[String] = {
    val files = ArrayBuffer.empty[String]
    class Visitor extends SimpleFileVisitor[Path]
    {
      override def visitFile(file: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (attr.isRegularFile && isImageFile(file.toString))
          files += file.toString
        FileVisitResult.CONTINUE
      }
      override def visitFileFailed(file: Path, ex: IOException) : FileVisitResult = {
        println(s"Exception: $file $ex")
        FileVisitResult.CONTINUE
      }
      override def preVisitDirectory(dir: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (shouldSkipDir(dir.toString))
          FileVisitResult.SKIP_SUBTREE
        else
          FileVisitResult.CONTINUE
      }
    }
    Files.walkFileTree(Paths.get(dir), new Visitor)
    files
  }

  def shouldSkipDir(dir: String) : Boolean = {
    dir.endsWith(".photolibrary")
  }
}