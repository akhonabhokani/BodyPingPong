Body PingPong revision 0.1

Key:
    BPP: Body PingPong

1. Requirements:
  1. JRE 1.5.0
  2.LWJGL (included in package in both source and binary packages for your convenience)

2. What's included with this package. 

    +data\                  #media files reside here
    +lib\
     |-- jar\               # LWJGL libraries
     |-- native             # Native LWJGL libraries
    - README.txt            # This file
    - Release Notes.txt     # ...
    - TODO.txt              # Stuff that's still to be implemented...
    - bpp.properties        # You do not need to modify this file, unless you know what you're doing.
                            # It's used as a configuration file for the game. More uses for it will 
                            # come up as time goes on!
    - java.policy           # This is needed by LWJGL for accessing devices, etc.
    - runBPP.bat            # Runs the Body PingPong game.
    - runTest.bat           # Tests your game controllers, for each connected controller a window 
							# created where you can see which buttons are pressed, etc. [taken from LWJGL]
    - LICENSE.txt           # Distribution license for BPP.
    - build.xml             # Only present in the source package distro for those who want to build the
							# source themselves.

    
3. To run Body PingPong:
   --------------------
    1) cd to the parent directory BodyPingPong\  
    2a) Windows: 
        double-click on runBPP.bat

    2b) Linux
        $ chmod +x runBPP-linux.sh
        $ runBPP-linux.sh


4. To build Body PingPong:
    ----------------------

* If you happen to have Ant installed, then you can use the build.xml script.
  Open it and see what options you've got. (compile, run, build)
* If you don't have Ant, you have two choices:
    ** download Ant yourself and use it as stated above! :~)
    the fact is: Ant is very easy to use, plus you'll have one more (development) tool in your arsenal!!