photos
======

Lists the images within a list of directories, along with the image creation date (if available) from the exif data.


Build
=====

    $ sbt
    > compile
    > run "/Users/foo/photos" <dir2> <dir3> ...
    [run-main-1] INFO Photos - Starting
    /Users/foo/photos/IMG_20140718_171700.jpg: Fri Jul 18 17:17:01 PDT 2014
    /Usses/foo/photos/DSC_0044.JPG: <no date>
    ...
    [run-main-0] INFO Photos - Processed 13224 files
