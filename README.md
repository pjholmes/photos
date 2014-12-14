photos
======

Lists the images within a list of directories, along with the image creation date (if available) from the exif data.


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
