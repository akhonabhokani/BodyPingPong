@echo off
java -cp .;dist\BodyPingPong.jar;data;lib\res;lib\jar\lwjgl.jar;lib\jar\jme.jar;lib\jar\jinput.jar;lib\jar\lwjgl_devil.jar;lib\jar\lwjgl_fmod3.jar;lib\jar\lwjgl_test.jar;lib\jar\lwjgl_util.jar;  -Djava.library.path=lib\native  -Djava.security.policy=java.policy za.co.meraka.Main