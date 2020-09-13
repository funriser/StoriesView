# StoriesView
[![](https://jitpack.io/v/funriser/StoriesView.svg)](https://jitpack.io/#funriser/StoriesView)

Custom view inspired by Instagram stories.

## Gradle
To get a StoriesView library into your build:

Step 1. Add in your top-level build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency to your module-level build.gradle:
```gradle
dependencies {
    implementation 'com.github.funriser:StoriesView:0.1.1-alpha'
}
```

## Manifest
Add Internet permissions to your manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## How to use?
### Just add stories view to your xml layout
```xml
<com.funrisestudio.stories.StoriesView
        android:id="@+id/vStories"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
### ...and control it from your Kotlin code
!Required! This call is required to initialize main components with given fragment activity:
```kotlin
vStories.init(this)
```

Bind a dataset that will be used to display images for stories:
```kotlin
vStories.setStories(STORIES)
```

(Optional) Set a listener which will be invoked when user finishes watching all the stories:
```kotlin
vStories.setOnStoryCompletedListener {
    Toast.makeText(this, "All stories watched", Toast.LENGTH_SHORT).show()
}
```
## Customization
### XML
* Height of the progress bar: ```app:progressHeight="[dimension]"```
* Progress bar top margin: ```app:progressMarginTop="[dimension]"```
* Progress bar left margin: ```app:progressMarginLeft="[dimension]"```
* Progress bar right margin: ```app:progressMarginRight="[dimension]"```
* Space between bars that represent stories: ```app:progressSpacing="[dimension]"```
* Color of the progress line: ```app:progressColor="[color]"```
* Color of the progress line background ```app:progressBackgroundColor="[color]"```
### Kotlin
You can customize view styling using params of StoriesView.init() method:
```kotlin
vStories.init(
    activity = this,
    progressHeight = Int,
    progressMarginTop = Int,
    progressMarginLeft = Int,
    progressMarginRight = Int,
    progressSpacing = Int,
    progressColor = Color,
    progressBackgroundColor = Color
)
```
