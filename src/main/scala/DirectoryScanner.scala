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
import grizzled.slf4j.Logger
import org.joda.time.LocalDateTime
import scala.collection.mutable.ArrayBuffer

object DirectoryScanner {
  val logger = Logger("DirectoryScanner")

  def isPhotoFile(fileName: String) = Config.extensions.exists(ext => {
    fileName.takeRight(ext.length()).toLowerCase == ext
  })

  def shouldSkipDir(dir: String) = Config.skipDirSuffixes.exists(dir.endsWith(_))

  def getPhotoFilesBelowPath(path: String) = {
    val files = ArrayBuffer.empty[Photo]
    class Visitor extends SimpleFileVisitor[Path]
    {
      override def visitFile(file: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (attr.isRegularFile && isPhotoFile(file.toString)) {
          // on my mac (OSX 10.10.1) this returns the last modification time, not create time
          val fileTime = attr.creationTime()
          files += new Photo(file.toString, attr.size(), new LocalDateTime(fileTime.toMillis))
        }
        FileVisitResult.CONTINUE
      }
      override def visitFileFailed(file: Path, ex: IOException) : FileVisitResult = {
        logger.error(s"Exception: $file $ex")
        FileVisitResult.CONTINUE
      }
      override def preVisitDirectory(dir: Path, attr: BasicFileAttributes) : FileVisitResult = {
        if (shouldSkipDir(dir.toString)) {
          logger.info(s"Skipping directory $dir")
          FileVisitResult.SKIP_SUBTREE
        }
        else {
          logger.debug(s"Entering: $dir")
          FileVisitResult.CONTINUE
        }
      }
    }
    logger.info(s"Scanning $path")
    Files.walkFileTree(Paths.get(path), new Visitor)
    files
  }
}







