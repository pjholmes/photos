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

object Photos {

  val logger = Logger("Photos")

  def main(args: Array[String]) {

    logger.info("Starting file scan")
    val files = args.flatMap(arg => getImageFilesBelowPath(arg))
    logger.info(s"Found ${files.length} files")

    logger.info(s"Getting file dates")
    files.foreach(println)
    logger.info(s"Processed ${files.length} files")
  }
}