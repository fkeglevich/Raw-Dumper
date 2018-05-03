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
3. Obtain some low-level camera sensor information from camera system's logcat.

A more detailed description is given below:

1. It requires it because the raw pictures are also automatically saved by the system as \*.i3av4 files to an inaccessible directory (usually inside /data). Each raw photo can easily occupy more than 10 megabytes, easily filling up the entire storage while also preventing the user from reclaiming that space. 

2. The camera can crash when taking raw pictures, however the \*.i3av4 file can still be present, so that file is used to generate a proper .dng file.

3. Certain features require a realtime reading of these low-level camera sensor information:

- **Total sensor gain**, for ISO metering
- **Integration time**, for shutter speed metering
- **AWB gains**, for correct white balance in the generated .dng files (and maybe color temperature metering in the future)

These information above appears on the logcat logs when the camera.hal.debug property is set to 2 before the camera is opened. So the app calls the logcat command in a background root shell to keep reading the camera's logs in realtime. While reading the logs the app uses a simple pattern matching to parse only the specific logs where the useful information listed above is stored. 

These are the **tag\:priority** pairs used to form the logcat command:
- Camera_AtomAIQ:D
- Camera_ISP:D
- \*:S

Some important notes:

- if some of the logcat-reading-features aren't used, the actual logcat command can be even stricter.
- If none of the logcat-reading-features are used, the logcat commmand is never used.
- Even though sensitive information may appear in the system logs, it won't be accessible to the app because the command discards everything that isn't camera-related.

For more information about the syntax of the logcat command, see the [documentation](https://developer.android.com/studio/command-line/logcat).
