/*
 * Copyright (c) 2006 Body PingPong Project (Meraka Institute)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'Body PingPong Project' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package za.co.meraka;

/* Standard imports.
 */

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.glu.GLU;
import java.io.FileInputStream;
import java.util.Properties;

/* Starting imports
 */
import static org.lwjgl.opengl.GL11.*;
import za.co.meraka.components.Ball;
import za.co.meraka.components.CentreQuad;
import za.co.meraka.fonts.BitMapFonts;
import za.co.meraka.fonts.Sprite;


/** $Id: BasicGame.java,v 1.15 2006/05/07 18:02:42 Ara Exp $
 *
 * This class is the base class for our game. It just contain intilization and cleanup things
 *  for OpenGL.
 *
 * @author Ara
 * @version $Revision: 1.15 $
 */

public class BasicGame {
    
    /** Displays game in fullscreen mode if true, windowed mode otherwise. */
    private boolean             fullscreen = true;
    
    /** A state for resumption of the game. */
    protected boolean           resume = false;
    
    /** For stopping the game after a player's has lost all points OR
     * When one player's reached the maximum score
     */
    protected boolean           stopped = false;
    
    /** Is game finished ? **/
    public boolean              finished;
    
    /** Width for windowed display mode */
    private int                 width;
    
    /** Height for windowed display mode */
    private int                 height;
    
    /** The ball. **/
    protected Ball              ball;
    
    /** Sprite messages */
    protected Sprite            message;
    
    
    /** The BitMapFonts reference for this game  */
    BitMapFonts bitMapFonts;
    
    
    /** A quad placed at the centre of the game. Players score a point when they
     * hit the ball and the ball touches this quad.
     */
    protected CentreQuad        cetreQuad;
    
    
    /**
     * Game title
     */
    public final String GAME_TITLE = "Body PingPong";
    
    /** Minimum number of controllers permisible for this game */
    protected final int         MIN_CONTROLLERS = 4;
    
    
    /** Array of game controllers registered by the system */
    protected Controller[]      controllers;
    
    /** Number of controllers for this game */
    protected int               numberOfControllers;
    
    /** The game's current display mode */
    protected DisplayMode       mode;
    
    
    /** BasicGame constructor.
     */
    
    public BasicGame() {
        try {
            // set properties
            
            FileInputStream propFile = new FileInputStream("bpp.properties");
            Properties p = new Properties(System.getProperties());
            p.load(propFile);
            
            // set the system properties
            System.setProperties(p);
            // display new properties
            //System.getProperties().list(System.out);
            
            
            // Create the windowed OpenGL window at location (50,50), size 800x600 and 16bit depth.
            DisplayMode modes[] = Display.getAvailableDisplayModes();
            int w=Integer.parseInt(System.getProperty("za.co.meraka.width", "1024"));
            int h=Integer.parseInt(System.getProperty("za.co.meraka.height", "768"));
            int bpp=Integer.parseInt(System.getProperty("za.co.meraka.bitsPerPixel", "16"));
            
            for(int i = 0; i < modes.length; i++) {
                //System.out.println("<<<" + modes[i] + ">>>");
                if(modes[i].getWidth() == w &&
                        modes[i].getHeight() == h &&
                        modes[i].getBitsPerPixel() == bpp) {
                    Display.setDisplayMode(modes[i]);
                    mode = Display.getDisplayMode();
                    break;
                }
            }
            
            Display.setTitle(GAME_TITLE );
            Display.setFullscreen(fullscreen);
            Display.setLocation(-3, 0);
            Display.create();
            //Display.sync(FRAMESPERSECOND);
            
            Keyboard.create();
            
            Controllers.create();
            
            numberOfControllers = Controllers.getControllerCount();
            if(numberOfControllers  < MIN_CONTROLLERS){
                System.out.println("[WARNING]: Not enough controllers for this game");
                
            }
            
            controllers = new Controller[numberOfControllers];
            
            for(int i=0; i<numberOfControllers; i++)
                controllers[i] = Controllers.getController(i);
            
        } catch(Exception e) {
            System.err.println("Failed to create OpenGL due to " + e);
            System.exit(1);
        }
    }
    
    
    /** Start OpenGL application.
     */
    
    public void start() {
        try {
            // Init OpenGL and game states.
            init();
            
            
            // Here is our main loop for game logic and rendering.
            while(!finished) {
                // Windows has been closed ?
                if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                    System.exit(0);
                
                if((Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0))
                && !isRunning()){
                    //message = null;
                    ball.start();
                }
                // Handle keyboard events.
                
                process_keyboard();
                
                //Handle joysticks events
                process_joysticks();
                
                // Draw game components on screen
                render();
                
                
                // Update windows graphics and swap OpenGL buffer.
                Display.update();
            }
        } catch(Throwable t) {
            // If an error occurs, just print the stack trace and exit the game.
            t.printStackTrace();
        } finally {
            // Clean all and exit.
            cleanup();
        }
    }
    
    
    
    /** Init OpenGL.
     */
    
    public void init() throws Exception {
        // Enable key buffering.
        //Keyboard.enableBuffer(); // not supported by this version of LWJGL
        
        glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        glClearDepth(1.0); // Depth Buffer Setup
        glDepthFunc(GL11.GL_LEQUAL);
        glDisable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_BLEND);
        glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        
        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
                45.0f,
                (float) mode.getWidth() / (float) mode.getHeight(),
                0.1f,100.0f);
        
        // Really Nice Perspective Calculations
        glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        
        // Go into 2D ortho mode.
        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();
        GLU.gluOrtho2D(0, mode.getWidth(), mode.getHeight(), 0);
        glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();
    }
    
    
    
    /** Rendering method.
     */
    
    public void render() {
    }
    
    
    
    /** Process keyboard events.
     */
    public void process_keyboard(){}
    
    /** Process controller events
     */
    public void process_joysticks(){}
    
    
    /** Checks if the game is running */
    private boolean isRunning(){
        return finished;
    }
    
    /** Attempts to switch display modes on the fly. [Not tested yet] */
    private void switchMode() {
        if(!fullscreen){
           fullscreen = true;
        }
        try {
            Display.setFullscreen(fullscreen);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public CentreQuad getCetreQuad() {
        return cetreQuad;
    }
    
    
    /**
     * Do any game-specific cleanup
     */
    public static void cleanup() {
        // TODO: save anything you want write to disk here
        // scores and such
        
        // Stop the sound
        AL.destroy();
        
        
        // Destroy the keyboard.
        Keyboard.destroy();
        
        //forward competibility: when controllers can be destroyable/distructible
        Controllers.destroy();
        
        // Close the window
        Display.destroy();
    }
    
    /** Returns a reference to this game's ball.
     * @return ball
     */
    public Ball getBall() {
        return ball;
    }
    
    
    /** A method to restart the game. */
    protected void restart(){};
    
    /** Ends current game (if running) */
    protected void stopGame(){}
}