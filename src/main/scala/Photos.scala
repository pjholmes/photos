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

import java.nio.file.{Paths, Files}
import grizzled.slf4j.Logger

class Photos(args: Array[String])  {
  import Photos._

  var album = new Album()

  val files = scan(args)

  // The source files could have many duplicates WITHIN the source files
  // (regardless of whether any of the source files duplicate files in the album).
  // Find all the duplicate files. For each set of duplicates, sort them by descending
  // date quality score. The tail of the list are all duplicates.
  //
  // If one file has exif data and the other does not, their sizes and digests would
  // not match so they would not be in the same set. However, if one file in the set
  // has a date in the file name, and another does not, we would prefer
  // the one with a date (as this is better representative of the capture
  // datetime).

  val dups = findDups()

  val filesToAdd = files.filter(f => !dups.contains(f.fileName))

  logger.info("Importing...")

  filesToAdd.foreach(photo => {
    try {
        album.addIfNotDuplicate(photo)
        if (Config.deleteSource) {
          photo.delete()
        }
    } catch {
      case e: Exception => logger.error(s"Exception: $e adding file: ${photo}")
    }
  })

  if (Config.deleteSource) {
    dups.foreach(dup => {
      try {
        Files.delete(Paths.get(dup))
      } catch {
        case e: Exception => logger.error(s"Exception: $e deleting file: ${dup}")
      }
    })
  }

  logger.info(s"Done - Imported ${album.getFilesAddedCount()} files.")

  def scan(args: Array[String]): Array[Photo] = {
    val files = args.flatMap(arg => DirectoryScanner.getPhotoFilesBelowPath(arg))
                    .filter(p => p.fileSize > Config.minimumSize)
    logger.info(s"Found ${files.length} photos")
    files
  }

  def findDups(): Set[String] = {
    logger.info("Looking for duplicates files within source files...")
    // Getting the file digest is expensive. So, find files that have duplicate sizes first.
    // Then incur the expense of getting the digest for just those files, and regroup by both size and digest.
    val grpBySize = files.groupBy(_.fileSize)
    // duplicates are groups with more than one file
    val grpsWithDups = grpBySize.filter(_._2.length > 1)
    // flatmap the files back into a single list so we can group them again
    val allDups = grpsWithDups.flatMap(_._2)
    // this will materialize the digest for only the dups - slowly
    val grpBySizeAndDigest = allDups.groupBy(p => (p.fileSize, p.fileDigest))
    // again, only groups with more than 1 file
    val sdGrpsWithDups = grpBySizeAndDigest.filter(_._2.size > 1)
    // create a list of lists of photos, sorted by (descending score, ascending date)
    val grpsOfDups = sdGrpsWithDups.map(_._2.toList.sorted)
    // Take the tail of each list,
    // which represents the files which duplicate the file at the head of the list
    grpsOfDups.foreach(g => {
      logger.info(s"File ${g.head.fileName} has the following duplicates:")
      g.tail.foreach(p => {
        logger.info(s" ${p.fileName}")
      })
    })
    // put them all into a single list and select just the file name
    val allDups2 = grpsOfDups.flatMap(p => p.tail).map(p => p.fileName).toSet
    logger.info(s"Total duplicate source files: ${allDups2.toArray.size}")

    allDups2
  }
}

object Photos  {
  val logger = Logger("Photos")

  def main(args: Array[String]) {
    new Photos(args)
  }
}
