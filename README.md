photos
======

Given a list of directories, lists the image files found below those directories, along with the image creation date (if available) from the file [Exif](http://en.wikipedia.org/wiki/Exchangeable_image_file_format) data.


build/run
=====

    $ sbt
    > compile
    > run "/Users/pjholmes/photos" <dir2> <dir3> ...
    [run-main-1] INFO Photos - Starting
    /Users/pjholmes/photos/IMG_20140718_171700.jpg: Fri Jul 18 17:17:00 PDT 2014
    /Usses/pjholmes/photos/DSC_0044.JPG: <no date>
    ...
    [run-main-0] INFO Photos - Processed 13224 files
    
credit
======

Thanks to [Drew Noakes](https://drewnoakes.com/code/exif/) for the [metadata extractor](https://github.com/drewnoakes/metadata-extractor)
