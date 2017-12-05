4# PhotozigChallenge
Programming Android challenge

This app shows a list of media files you can download and play.

Created in Android Studio 3.0.1.

## Some libraries used on the project

1. [Retrofit](http://square.github.io/retrofit/) to make API requests.
2. [EventBus](http://greenrobot.org/eventbus/) for publisher/subscriber pattern.
3. [Butterknife](http://jakewharton.github.io/butterknife/) for view binding.
3. [Picasso](http://square.github.io/picasso/) to download images.
4. Others.


## Target SDK Version 

24

## Min SDK Version

15

## Initial Setup

### Java
Download and install [Java Development Kit](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html).

You can check your version with *java -version*

### Android
Download and install [Android Studio](https://developer.android.com/studio/index.html?hl=pt-br) along with SDK 24.

### Git
Download and install [git](https://git-scm.com/).

You can check your version with `git --version`

##### Configuration

Create a [Github](https://github.com/) account if you don't have one.

Run this from your terminal, changing what's inside the quotes, to configure your credentials

`git config --global user.name "Your Name Here"`

`git config --global user.email "your_email@youremail.com"`

## Importing the project

1. First you have to clone the repository. Navigate to the folder you want to download the project and run `git clone https://github.com/icaropj/PhotozigChallenge.git/` from your terminal 

2. Open Android Studio, click on *Open an existing Android Studio project* and select the project folder. Wait for the project to import.

3. Create an emulator(Api 24) if you don't have one already, or enable ADB integration on Tools > Android > Enable ADB Integration and plugin your device.

4. Run the project picking the device you chose on the previous step.

