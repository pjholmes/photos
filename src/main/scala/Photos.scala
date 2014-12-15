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

import ImageScanner._
import grizzled.slf4j.Logger

import scala.collection.immutable.Iterable

object Photos {
  val logger = Logger("Photos")

  def main(args: Array[String]) {

    val photos = new Photos()
    photos.scan(args)

    val dups = photos.findDups()
    // print the duplicates in groups separated by linefeed
    dups.foreach(grp => {
      println()
      grp.foreach(p => {
        println(p.fileName)
      })
    })
  }
}

class Photos() {
  var files : Array[Photo] = _

  def scan(args: Array[String]) = {
    logger.info("Scanning ...")
    files = args.flatMap(arg => getImageFilesBelowPath(arg))
    logger.info(s"Found ${files.length} files")
  }

  def findDups(): Iterable[Iterable[Photo]] = {
    logger.info("Finding duplicate photos ...")
    // Getting the file digest is expensive. So, find files that have duplicate sizes first.
    // Then incur the expense of getting the digest for just those files, and regroup by both size and digest.
    val grpBySize = files.groupBy(_.size)
    // duplicates are groups with more than one file
    val dups = grpBySize.filter(_._2.length > 1)
    // flatmap the files back into a single list so we can group them again
    val allDups = dups.flatMap(_._2)
    // this will materialize the digest for only the dups
    val grpBySizeAndDigest = allDups.groupBy(p => (p.size, p.digest))
    // again, only groups with more than 1 file
    val dups2 = grpBySizeAndDigest.filter(_._2.size > 1)
    val res = dups2.map(_._2)
    val total = res.map(_.size).sum
    logger.info(s"Found ${total - res.size} duplicate photos")
    res
  }
}
