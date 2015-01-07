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
import java.nio.file._
import org.joda.time.format.DateTimeFormat
import scala.collection.mutable.ArrayBuffer

/**
 * An album represents the directory we are importing into. It keeps track of
 * all the photo files below the directory path and keeps an index of them by size.
 * It add files is they are not duplicates of existing files (regardless of name),
 * and updates the index.
 */
class Album {
  import Album._

  private val albumDir = Config.albumDir
  logger.info(s"Album dir: $albumDir")

  ensureDirExists(Paths.get(albumDir))

  private val files = DirectoryScanner.getPhotoFilesBelowPath(albumDir)
  logger.info(s"Found ${files.length} photos in album")

  // Index all the files by size. Before copying any files into the album,
  // check this to look for a file with the same size, then check the digest
  private var grpBySize = files.groupBy(_.fileSize)

  private var filesAddedCount = 0

  /**
   * If the photo is not a duplicate of a file already in the album, then copy it into the album and
   * update the index.
   * @param photo The photo to be added
   */
  def addIfNotDuplicate(photo: Photo): Unit = {
    if (isDuplicate(photo)) {
      return
    }

    filesAddedCount += 1

    val destDir = targetDir(photo)
    ensureDirExists(destDir)

    var seq = 0
    var destPath = Paths.get(destDir.toString, targetName(photo, seq))
    while (Files.exists(destPath)) {
      seq += 1
      destPath = Paths.get(destDir.toString, targetName(photo, seq))
    }

    logger.info(s"Copying to $destPath")

    try {
      Files.copy(Paths.get(photo.fileName), destPath, StandardCopyOption.COPY_ATTRIBUTES)
    } catch {
      case e: Exception => logger.error(s"Exception: $e while copying from: ${photo.fileName} to ${destPath}")
    }

    // Update the index with the added photo
    val files = grpBySize.getOrElse(photo.fileSize, new ArrayBuffer[Photo]())
    files += new Photo(destPath.toString, photo.fileSize, photo.bestCaptureDateTime)
    grpBySize = grpBySize.updated(photo.fileSize, files)
  }

  def getFilesAddedCount()  = filesAddedCount

  /**
   * If albumDir exists but is not a directory, throw. If it does not exist, create it.
   */
  private def ensureDirExists(path: Path) = {
    if (Files.exists(path)) {
      if (! Files.isDirectory(path)) {
        throw new IllegalArgumentException(s"The albumDir: $albumDir is not a directory")
      }
    } else {
      Files.createDirectories(path)
    }
  }

  /**
   * Return true if the photo is a duplicate of one already in the album. To be a dup it must have the
   * same size and fileDigest.
   * @param photo the photo to be checked for duplicate
   * @return true if the file is a duplicate of opne already in the album
   */
  private def isDuplicate(photo: Photo) : Boolean = {
      // avoid computing the file digest if there are no files with the same size
      grpBySize.getOrElse(photo.fileSize, new ArrayBuffer[Photo]()).foreach(file => {
        if (file.fileDigest == photo.fileDigest) {
          logger.info(s"File ${photo.fileName} appears to be a duplicate of file ${file.fileName}")
          return true
        }
      })
     false
  }

  /**
   * Return the target file name for a given photo and sequence number.
   * @param photo The photo object
   * @param seq The sequence number to be added to the end of the file to prevent name collision
   * @return The target file name for the photo
   */
  private def targetName(photo: Photo, seq: Int) : String = {
    if (seq == 0) {
      photo.bestCaptureDateTime.toString(Album.fileNameFormat) + photo.extension
    } else {
      s"${photo.bestCaptureDateTime.toString(Album.fileNameFormat)}_$seq${photo.extension}"
    }
  }

  /**
   * Return the directory name where the file should be located. Currently this is
   * /{album}/yyyy/MM.
   * @param photo The photo object
   * @return A Path representing the directory where the file should be located
   */
  // The destination path of the photo - e.g. /{album}/2014/12
  private def targetDir(photo: Photo): Path = {
    Paths.get(albumDir,
      photo.bestCaptureDateTime.getYear.toString,
      f"${photo.bestCaptureDateTime.getMonthOfYear}%02d")
  }
}

object Album {
  val logger = Logger("Photo")
  val fileNameFormat = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss")
}
