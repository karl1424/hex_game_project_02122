# hex_game_02122
 
We are using
- java 23.0.1
- Apache Maven 3.9.9
- g++ 15.1.0
- Visual Studio Code
But later versions might work as well.

You can download Java from Oracle. Remember to add the jdk-23 folder (NOT the bin) to the JAVA_HOME system variable.

You can download Maven from the following website: https://maven.apache.org/download.cgi
Download the bin.zip-file and unpack the content. Add the bin folder to your PATH-variables.

To download jSpace go to: https://github.com/pSpaces/jSpace
Download the zip file. Unpack the content and add the bin folder to your PATH-variables.
Open the terminal and navigate into the unpacked folder named jSpace-master containing the pom.xml file. Here you should run the following commands:
- mvn clean verify
- mvn install

To download C++ go to: https://winlibs.com/
Click on "Download it here". Unpack the content and add the bin folder to your PATH-variables.

Open the project in your Visual Studio Code. Install the extension pack "Extension Pack for Java".


To run the project without Online Multiplayer go to the following in the left side bar:
MAVEN > project_02122 > Plugins > javafx > run


To run the project with Online Multiplayer (localhost) find Server.java in the package network. Right click on Server.java and Run Java.
The server is now running. Now do as in the previous step and run the project. To run another instance of the program copy the command
to run the first instance and paste into a new terminal.
To run with an online server go to SpaceTag.java in the network/tags and change SERVER_IP to your IPv4 address as a String.


To run the project using the C++ version of MCTS go to ComputerManager.java in the package computer_opponent.
Change the boolean nativeTest to true. Make sure to compile the C++ by navigating to hex_game_project_02122 folder in a PowerShell and run the following commands:

- javac -h native/include src/main/java/dk/dtu/main/NativeWrapper.java
- g++ -O3 -march=native -std=c++17 -I"native/include" -I"C:\Program Files\Java\jdk-23\include" -I"C:\Program Files\Java\jdk-23\include\win32" -shared -o native/mylib.dll native/src/mylib.cpp native/src/MCTS.cpp native/src/MCTSNode.cpp native/src/MCTSUtils.cpp

You can now run the project as usual.
