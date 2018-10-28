Crimp Bit Analysis Tool
=======================

A recorder and analyzer for [MYO Armands](https://www.myo.com/) created as part of a course at [University of Bremen](https://www.uni-bremen.de/). Details can be found in the [final report](https://github.com/schulzp/uni-myo-recorder/raw/gh-pages/documents/Classification_of_Climbing_Grab_Movements_Measured_with_Myo_Armbands.pdf).

## Project Structure

The project consists of multiple modules:

* **`core`** application core providing the required services
* **`jfx`** graphical user interface based on JavaFX

I recommend JetBrains' IntelliJ IDEA. Simply import this folder as gradle project.

## Setup and Launch

The application expects a [mongodb](https://www.mongodb.com/) instance running.
The database settings are defined in `core/src/resources/database.properties`.

Once `mongod` is running `edu.crimpbit.analysis.AnalysisApplication` can be launched.

![Application Screenshot](https://github.com/schulzp/uni-myo-recorder/raw/gh-pages/images/application-screenshot.png)
