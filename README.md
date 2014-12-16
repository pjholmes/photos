photos
======

This is the start of a program to manage photos, including renaming them, putting them in a directory structure, removing duplicates, etc.

For now, given a list of directories, it lists all the sets of duplicate photos. A file is considered a duplicate if the file size and MD5 digest match. It currently does not move, copy, or delete any files.

Photo objects also contains the [Exif](http://en.wikipedia.org/wiki/Exchangeable_image_file_format) image creation date, if available, which is used to create the target file name. This name will be used in the future for moving files.

build/run
=====

    $ sbt
    > compile
    > run "/Users/user/photos" <dir2> <dir3> ...
    [main] INFO ImageScanner - Scanning ...
    [main] INFO ImageScanner - Found 3055 files
    [main] INFO ImageScanner - Finding duplicate photos ...
    [main] INFO ImageScanner - Found 259 duplicate photos

    /Users/user/photos/Christmas 2005/Christmas 2005 017.jpg
    /Users/user/photos/My Pictures/APRIL 7 2005 017.jpg

    /Users/user/photos/Eagle Scout/DCP_1052.JPG
    /Users/user/photos/Jeff's Eagle/DCP_1052.JPG

    
credit
======

Thanks to [Drew Noakes](https://drewnoakes.com/code/exif/) for the [metadata extractor](https://github.com/drewnoakes/metadata-extractor)
