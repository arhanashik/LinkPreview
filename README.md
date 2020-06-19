# LinkPreview

An android library to add link preview in any android application. This library uses the latest kotlin language including **coroutine**, **coil** and google's **material** design library.

[![Latest version](https://jitpack.io/v/arhanashik/LinkPreview.svg)](https://jitpack.io/#arhanashik/LinkPreview)
![Total download](https://img.shields.io/github/downloads/arhanashik/LinkPreview/total.svg)
![Code Size](https://img.shields.io/github/languages/code-size/arhanashik/LinkPreview)
![License](https://img.shields.io/github/license/arhanashik/LinkPreview)

## Preview
<img src="app/sampledata/screenshot.png" alt="Screen Shot" width="250"/>

## How to use
**Step 1:**
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency:
```
implementation 'com.github.arhanashik:LinkPreview:1.0.0'
```
**Step 2:** This library uses new **coil** library for image loader. You need to add the following for Java 8 support for coil:
```
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
**Step 3:** Google's **material** design library is used in this project which is really good to use in any project. Add the following dependency:
```
implementation 'com.google.android.material:material:1.3.0-alpha01'
```
And then change the **style.xml** file inside values folder to use material theme. Replace base application theme with the following:
```
<!-- Base application theme. -->
<style name="AppTheme" parent="Theme.MaterialComponents.Light.DarkActionBar">
    <!-- Customize your theme here. -->
    <item name="colorPrimary">@color/colorPrimary</item>
    <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
    <item name="colorAccent">@color/colorAccent</item>
</style>
```
**Step 4:** Add internet permission in manifest file
```
<uses-permission android:name="android.permission.INTERNET"/>
```
That's it. You are good to go!

Add **LinkPreview** widget in XML as below
```
<com.workfort.linkpreview.LinkPreview
    android:id="@+id/linkPreview"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:url="https://stackoverflow.com" />
```
You can change the url at runtime from your java/kotin code.
```
linkPreview.load("your-url")
```


## License
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/