Crimp Bit Analysis Tool
=======================

## Project Structure

The project consists of multiple modules:

* **`core`** application core providing the required services
* **`jfx`** graphical user interface based on JavaFX

I recommend JetBrains' IntelliJ IDEA. Simply import this folder as gradle project.

## Setup and Launch

The application expects a [mongodb](https://www.mongodb.com/) instance running.
The database settings are defined in `core/src/resources/database.properties`.

Once `mongod` is running `edu.crimpbit.analysis.AnalysisApplication` can be launched.
