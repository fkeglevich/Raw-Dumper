# Privacy Policy

Raw Dumper is an app for taking raw pictures, it doesn't collect any personal information about you. 

It requires camera permission for taking pictures.

It requires write external storage permission for saving the pictures.

It requires root because the raw pictures are also automatically saved by the system as *.i3av4 files to an inaccessible directory (usually inside /data). Each raw photo can easily occupy more than 10 megabytes, easily filling up the entire storage while also preventing the user from reclaiming that space. 

Raw Dumper uses root permission to:
* Delete the unwanted copy of the pictures inside a device-dependent inaccessible directory;
* In some circumstances, a *.i3av4 file is the only available copy of the picture, so Raw Dumper grabs it to generate a proper .dng file.
