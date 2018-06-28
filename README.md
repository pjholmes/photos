photos
======

## The Problem

I had thousands of photos on hundreds of directories on many different drives. The directories had no pattern, e.g:

1. /photos from last Christmas
2. /graduation pictures
3. /the_Wedding
4. /pictures from 21st-Birthday-Party

The files were captured by all different cameras, and had very different file name patterns. Many had [Exif](http://en.wikipedia.org/wiki/Exchangeable_image_file_format) data and some did not. Some files without Exif data had the capture timestamp in the title (e.g. photo-2002-03-04-05-06-21.jpg). Some of the photos had many duplicates, all with different names and in different directories.

## My solution

This programs solves the problem for me by doing the following:

1. Scans all of the directories in the argument list, but skips over any iPhoto libraries
2. Finds all of the image files in those directories, skipping any small files (configurable) which could be thumbnails or icons.
3. For each file, determines the best available date and time to use as the capture timestamp (Exif, file name, file creation timestamp)
4. If there are duplicates of files, takes the one from each set of duplicates with the most reliable timestamp
5. Checks to see if the file is a duplicate of any files already in the target directory. If not:
6. Renames the file using the timestamp
7. Puts the file in the directory {album}/YYYY/MM/. So, the resulting location might be: /{album}/2004/04/2004-04-21_10-53-21.jpg
8. If multiple files have the same timestamp, adds a sequence number in the file name
9. Optionally (configurable) deletes the source photos

## To Use
1. Update the application.conf file. Update the albumDir to the location where you want the files copied to.
2. Compile
3. Run, passing in the names of directories to be scanned.
4. After confirming the program meets your needs, you could update the "deleteSource" configuration parameter to "true". This will delete the files from the source directories after they have been copied or confirmed to be duplicates. 

## Warning
1. The program creates directories and files, and (if configured), deletes files. Please ensure you have backups of all files. 2. Test the program thoroughly to ensure it meets your requirements.
3. The program is designed to lose information. For example, if you had pictures in a directory named "My 21st Birthday photos" which were taken in December of 2014, they will be copied to a directory named "2014/12". And a photo named "Aunt_Martha.jpg" may now be "2014_11_25-10_30_53.jpg". In both cases, information (that these photos were from a birthday party, or that the person was dear aunt Martha) has been lost. This is what I wanted, but may be unacceptable for you.

## Limitations
1. After deleting source files, some directories may be empty. Ideally, the program would detect this and delete them.
2. A file is only considered duplicate if it has the exact same size and content as another file (technically the same size and MD5 hash of the file content). The program does not look for images which could be same image with differnt dimensions. Note however, that if a single image was used to create the other images (crop, red eye, etc.) and that image has Exif data, and the date was retained in the editing process, then all of the photos will have the same base name, varying only by sequence number, and will sort together in a file list.
3. The program was tested on a MAC (10.10.1). It has not beed tested on any other OS.

## build/run

    $ sbt
    > compile
    > run "/Volumes/Personal/My Pictures"
    [main] INFO Photo - Album dir: /Users/pjholmes/Photos
    [main] INFO DirectoryScanner - Scanning /Users/pjholmes/Photos
    [main] INFO Photo - Found 3524 photos in album
    [main] INFO DirectoryScanner - Scanning /Volumes/Personal/My Pictures
    [main] INFO Photos - Found 1087 photos
    [main] INFO Photos - Looking for duplicates files within source files...
    [main] INFO Photos - File /Volumes/Personal/My Pictures/misc/Originals/party1.bmp has the following duplicates:
    [main] INFO Photos -     /Volumes/Personal/My Pictures/misc/Originals/copy of party1.bmp
    ... 
    [main] INFO Photos - Total duplicate source files: 14
    [main] INFO Photos - Importing...
    [main] INFO Photo - File /Volumes/Personal/My Pictures/party1.bmp appears to be a duplicate of file /Users/pjholmes/Photos/2007/06/2007-06-23_08-55-53.jpg
    [main] INFO Photo - Copying to /Users/pjholmes/Photos/2007/06/2007-06-23_08-58-00.jpg
    ...
    [main] INFO Photos - Done - Imported 1069 files.

## credit

Thanks to [Drew Noakes](https://drewnoakes.com/code/exif/) for the [metadata extractor](https://github.com/drewnoakes/metadata-extractor)
