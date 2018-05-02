# Privacy Policy

Raw Dumper is an app for taking raw pictures, it doesn't collect any personal information about you.

It does not transmit any information over the internet.

### Required permissions:
* The camera permission is used for taking pictures.
* The write external storage permission is used for saving the pictures.

### About root access:

Root access is used to:
1. Delete the unwanted copy of the pictures inside a device-dependent inaccessible directory;
2. In some circumstances, a \*.i3av4 file is the only available copy of the picture, so Raw Dumper reads it to generate a proper .dng file.
3. Gets some low-level camera sensor information from camera system's logcat.

A more detailed description is given below:

1. It requires it because the raw pictures are also automatically saved by the system as \*.i3av4 files to an inaccessible directory (usually inside /data). Each raw photo can easily occupy more than 10 megabytes, easily filling up the entire storage while also preventing the user from reclaiming that space. 

2. The camera can crash when taking raw pictures, however the \*.i3av4 file can still be present, so that file is used to generate a proper .dng file.

3. TODO: explain the information collected from the logcat
