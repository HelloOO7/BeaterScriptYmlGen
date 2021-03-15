# BeaterScriptYmlGen
A tool to generate BeaterScript YAML headers from PlatinumMaster's Google sheet.

## Purpose
BsYmlGen is a simple script that uses the Google Sheets API to convert a table with specific row and column data to BeaterScript's YAML include headers. It shouldn't be necessary for general public usage.

## Setup
1. Clone the repository.
2. Create a Google Cloud Platform Sheets API application at https://developers.google.com/sheets/api/quickstart/java with the name `Bs2Yml` and download the `credentials.json` file.
3. Copy the `credentials.json` over to `BeaterScriptYmlGen/src/main/resources/credentials.json`.
4. Build the project with a Gradle-compatible IDE.

## Usage
1. Just run `BsYmlGen.java` lol.
2. The converted sheets will be in the program's working directory (most likely the project root).
