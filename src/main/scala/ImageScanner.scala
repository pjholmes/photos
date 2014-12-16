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

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import java.util.Date
import grizzled.slf4j.Logger
import scala.collection.mutable.ArrayBuffer

object ImageScanner {
  val logger = Logger("ImageScanner")

  val exts = List(".jpg", ".png", ".bmp")

  def isImageFile(fileName: String) = exts.contains(fileName.takeRight(4).toLowerCase)

  def shouldSkipDir(dir: String) = dir.endsWith(".photolibrary")  // skip iPhoto database

  def getImageFilesBelowPath(path: String) = {
    val files = ArrayBuffer.empty[Photo]
    class Visitor extends SimpleFileVisitor[Path]
    {
      override def visitFile(file: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (attr.isRegularFile && isImageFile(file.toString))
          files += new Photo(file.toString, attr.size(), new Date(attr.creationTime().toMillis))
        FileVisitResult.CONTINUE
      }
      override def visitFileFailed(file: Path, ex: IOException) : FileVisitResult = {
        logger.error(s"Exception: $file $ex")
        FileVisitResult.CONTINUE
      }
      override def preVisitDirectory(dir: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (shouldSkipDir(dir.toString))
          FileVisitResult.SKIP_SUBTREE
        else {
          logger.debug(s"Entering: $dir")
          FileVisitResult.CONTINUE
        }
      }
    }
    Files.walkFileTree(Paths.get(path), new Visitor)
    files
  }
}







