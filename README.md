Crimp Bit Analysis Tool
=======================

A recorder and analyzer for [MYO Armands](https://www.myo.com/) created as part of a course at [University of Bremen](https://www.uni-bremen.de/). Details can be found in the [final report](https://github.com/schulzp/uni-myo-recorder/raw/gh-pages/documents/Classification_of_Climbing_Grab_Movements_Measured_with_Myo_Armbands.pdf).

## Project Structure

The project consists of multiple modules:

* **`core`** application core providing the required services
* **`jfx`** graphical user interface based on JavaFX

I recommend JetBrains' IntelliJ IDEA. Simply import this folder as gradle project.

## Build and Launch

As mentioned above, this is a JavaFX application which requires a JavaFX runtime (`jfxrt.rt`).
During the build process gradle tries to locate that file in number of locations,
see [build.gradle](https://github.com/schulzp/uni-myo-recorder/blob/5ef54d3fa5bb751c5550fa5522b916eacfa007a2/jfx/build.gradle#L53).
The location of library [changed](https://stackoverflow.com/a/22516005/1774068) from JDK 7 to 8.

In order to build and run the application with gradle on Java 8 open `~/.gradle/gradle.properties`
and add `org.gradle.java.home=/path/to/java/home`, for example, on macOS this path is
`/Library/Java/JavaVirtualMachines/jdk1.8.0_161.jdk/Contents/Home/`. 

### Environment

The application expects...

* ...a [mongodb](https://www.mongodb.com/) instance.
  The database settings are defined in `core/src/resources/database.properties`.
* ...the [myo connect](https://support.getmyo.com/hc/en-us/articles/360018409792-Myo-Connect-SDK-and-firmware-downloads)
  service.

Only if all of these services are available `edu.crimpbit.analysis.AnalysisApplication` can be build and launched:

```
$ ./gradlew run
```

![Application Screenshot](https://github.com/schulzp/uni-myo-recorder/raw/gh-pages/images/application-screenshot.png)
