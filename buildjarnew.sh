
#### make sure the JDK Version of the Compiler IS EQUAL TO JRE version on the Server
## Current on Server
#openjdk version "1.8.0_292"
#OpenJDK Runtime Environment (AdoptOpenJDK)(build 1.8.0_292-b10)
#OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.292-b10, mixed mode)
## Current in IDE -> Preferences -> Build,xxx -> Java Compiler -> TValue: 8

# cd to project root folder: TValue
# run with command => ./buildjarnew.sh
mvn package
# This creates a jar: TValue-1.0-SNAPSHOT.jar
# Rename it
mv target/TValue-1.0-SNAPSHOT.jar ./TValue.jar
# Then run the program with command: java -jar TValue.jar
# make sure the jar has to exist in the folder where the above command is run.
