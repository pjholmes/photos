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

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object Config {
    private val config  = ConfigFactory.load()

    // to be considered a photo, it must have one of these extensions
    val extensions = config.getStringList("photos.extensions").asScala.map("."+_.toLowerCase)

    // when scanning for photo files, skip directories which have these extensions
    val skipDirSuffixes = config.getStringList("photos.skipDirSuffixes").asScala

    // the directory where files will be copied to (and checked for duplicates)
    val albumDir = config.getString("photos.albumDir")

    // Whether to delete source files
    val deleteSource = config.getBoolean("photos.deleteSource")

    // don't copy a file unless it's at least this big
    val minimumSize = config.getInt("photos.minimumSize") * 1000
}
