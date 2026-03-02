# WhiteboardApp


This app has been developed as a part of our bachelor project.
The goal of this app is to digitize a whiteboard in real-time using a smartphone camera.
This has been accomplished using OpenGL ES 2.0 and 3.0, the Paddle Paddle v2 Human Segmentor and OpenCV.

The app has been written using a pipeline that uses stages to accomplish these goals.
This is to make the pipeline highly reconfigurable to quickly test new changes.
The final pipeline can be seen below.

![The final pipeline](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/main/images/pipeline.png?raw=true)

We first get the image from the camera using the CameraXStage.
Then we use the Paddle Paddle Segmentor to create a mask of where the human is and then remove the human from the image using a previous image where the human is not in the masked area.
Afterwards this is sent to corner detection to find the corners of the whiteboard.
This is done in a separate thread as it is by far the slowest part of the pipeline.
This means that corner detection is not always completely up to date with the latest input image.
The corners from corner detection and the image from the masking step is then used in perspective correction where the whiteboard is perspective corrected and cropped to only include the whiteboard.
After the perspective correction step the image is binarized to remove smudges, glare, shadows and other unwanted stuff.
This produces a black and white image, therefore we add colors back in by using the binarized image as a mask.

The pipeline implementation can be found in the [Pipeline file](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/main/app/src/main/java/dk/scuffed/whiteboardapp/pipeline/Pipeline.kt). 


Latency test have been made in pullrequest [#78](https://github.com/cs-23-sw-6-21/WhiteboardApp/pull/78) and while using a Samsung Galaxy S10 (2019) we got around 25 fps on the full pipeline with 1920x1080 resolution and doublebuffering enabled. 
The init function sets up the whole pipeline. To modify the pipeline, just remove or add stages in this function, or any of the functions it calls. 


To do latency testing we recommend adjusting the pipeline to whatever stages you are interested in testing. Furthermore it may be beneficial to use the [MainThreadPipeline.kt](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/csv-timing-output/app/src/main/java/dk/scuffed/whiteboardapp/pipeline/stage_combinations/MainThreadPipeLine.kt) file, as this will run all of the stages on a single thread, which makes it easier to test.
Its all important to set the variables in the [CSVWriter.kt](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/csv-timing-output/app/src/main/java/dk/scuffed/whiteboardapp/pipeline/CSVWriter.kt) file.
These variables will allow you to set whether you're recording frame time for the stage or the overall program, if you want to run with glFinish (recommended only getting accurate frame time for the individual stages) and lastly for how many frames you want to record the data.
The double buffering and 4k parameters can also be set at the top of the [Pipeline.kt](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/csv-timing-output/app/src/main/java/dk/scuffed/whiteboardapp/pipeline/Pipeline.kt).

The middle button in the app will dissapear once the app is done with writing to the csv file.

All of the data is written to a CSV file called "Latency-Data.csv" that will be placed in the phones root folder.

## Structure
Almost everything that is interesting happens in [Pipeline file](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/main/app/src/main/java/dk/scuffed/whiteboardapp/pipeline/Pipeline.kt).
Here, fullPipeline is called by default, which constructs all stages the entire system.
You can explore all stages and groups of stages by simply following the calls made in fullPipeline.
Notice that stages that inherit from GLOutputStage have a lot of their functionality in their accompanying shader.
The used shader can be seen in its constructor, and all shaders are in the [shader folder](https://github.com/cs-23-sw-6-21/WhiteboardApp/blob/main/app/src/main/res/raw).
The rest of the program is mostly implementation details that are not really relevant for the paper, such as setting up OpenGLES, utilities and stuff for testing purposes.

## Building

Download the project and open it in android studio.
Compile it and launch on an Android device. It should work on any modern Android device.
The app requires an arm device, it cannot run in the emulator as that is x86.

!!IMPORTANT: if there is an android resource linking exception, you must COMPLETELY rebuild the project!!
Building the project normally again is not enough, it must be a complete rebuild.
We are not sure why this happens, but "build>rebuild project" should fix it.

## Instructions

Point the camera at a whiteboard with at least 3 corners visible. It can be difficult to tell if all are visible, in that case, check with your camera app to make sure.
The app has issues with corner detection if the background is too busy or the edges of the whiteboard is not visible.
If corner detection fails you can switch to manual corner setup:
The looped square icon switches between letting the user drag the corners manually and automatic corner detection.
The "full screen" button activates any `dumpToGalleryFull` in the pipeline. This saves them to the `/sdcard/Pictures/WhiteboardApp` folder.
There are no `dumpToGalleryFull` by default in the main branch.
