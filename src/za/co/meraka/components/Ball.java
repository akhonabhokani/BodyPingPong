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
package za.co.meraka.components;


import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import java.util.Vector;
import za.co.meraka.*;
import za.co.meraka.pointsystem.PointsCounter;


/** $Id: Ball.java,v 1.12 2006/05/07 18:02:40 Ara Exp $
 *
 * The ball class, which is in really a "squared ball", like in the original pong game.
 *
 * @author Ara
 * @version $Revision: 1.12 $
 */

public class Ball {
    /** The game this ball belongs to */
    BasicGame game;
    
    /** Centre quad */
    CentreQuad              cntrQuad;
    
    /** Location on X axis (this point is the middle of the ball). **/
    public float            locationX;
    
    /** Location on Y axis (this point is the middle of the ball). **/
    public float            locationY;
    
    /** The ball speed on X axis. **/
    public float            speedX = 0;
    
    /** The ball speed on Y axis. **/
    public float            speedY = 0;
    
    /** Direction */
    private int             direction = -1;//0 is a valid direction, hence the -1
    
    /** Display mode needed to get screen bounderies */
    protected DisplayMode   mode;
    
    
    /** The last paddle to touch this ball */
    private Paddle          lastTouched = null;
    
    /** An array of paddles. */
    private Paddle[]                paddle = null;
    
    /** true if the ball has already bounced, so as to prevent the points growing
     * too fast for one player.
     */
    private boolean         ballTouchedCntrQuad = false;
    
    /**  A reference to a PointsCounter object */
    private PointsCounter   pointsCounter;
    
    /** A reference to the sound manager objects.
     * The intervals between playing sound effects for the border and the paddle
     * is pretty short and using one sound manager has resulted in some nasty
     * behaviors before.
     *
     */
    private SoundManager    soundManager;
    
    
    /** An array of available sounds (actually the handles) */
    private int[]           soundsArray;
    
    
    /*
     * Needed by the tricks to solve the sound engine bug (OpenAl error).
     */
    
    /** Gets the system's time resolution */
    private long            timerTicksPerSecond	= Sys.getTimerResolution();
    
    /** Last time this ball was hit by a paddle */
    private long            lastHit             = System.currentTimeMillis();
    
    /** The interval between our players shot (ms) */
    private long            hittingInterval	= 500; // 1/2 a second
    
    
    /** A flag which indicates whether the ball is on the rebound or not */
    private boolean         onRebound           = false;
    
    /** A float value for normalising the FPS interval which controls the speed
     * of the ball.
     */
    private float           normalFpsInterval   = 0.0020f; // [properties file]
    
    /** Ball constructor.
     */
    public Ball(Main game, DisplayMode mode) {
        this.mode = mode;
        this.game = game;
        this.cntrQuad = this.game.getCetreQuad();
    }
    
    
    
    /** Reset the ball's location on screen.
     */
    
    public void reset() {
        locationX = mode.getWidth()/2;
        locationY = mode.getHeight()/2 - 60;
        speedX = 0;
        speedY = 0;
    }
    
    
    
    /** Start moving the ball in a random direction.
     */
    
    public void start() {
        // It's not a fully random start. Let me explain : the ball can start in four
        // possible direction : Up-Left, Up-Right, Down-Right or Down-Left.
        
        int random = (int)(Math.random() * 3);
        
        // The random number is 0, so the ball will start moving Up-Left :
        if(random == 0) {
            speedX = -150f * FPSCounter.frameInterval;
            speedY =  150f * FPSCounter.frameInterval;
        }
        // The random number is 1, so the ball will start moving Up-Right :
        else if(random == 1) {
            speedX =  150f * FPSCounter.frameInterval;
            speedY =  150f * FPSCounter.frameInterval;
        }
        // The random number is 2, so the ball will start moving Down-Right
        else if(random == 2) {
            speedX =  150f * FPSCounter.frameInterval;
            speedY = -150f * FPSCounter.frameInterval;
        }
        // The random number is 3, so the ball will start moving Down-Left
        else if(random == 3) {
            speedX = -150f * FPSCounter.frameInterval;
            speedY = -150f * FPSCounter.frameInterval;
        }
    }
    
    
    
    /** Check if the ball collide with a paddle.
     */
    
    public boolean collideWithPaddle(Paddle paddle) {
        // Test collision according to the paddle radius
        
        //first test which paddle it is:
        if(paddle instanceof VerticalPaddle){
            if(locationX <= paddle.locationX + 10 &&
                    locationX >= paddle.locationX - 10 &&
                    locationY <= paddle.locationY + 103.6f &&
                    locationY >= paddle.locationY - 103.6f) {
                return true;
            }
            
        } else if (paddle instanceof HorizontalPaddle){
            if(locationX <= paddle.locationX + 102.4f &&
                    locationX >= paddle.locationX - 102.4f &&
                    locationY <= paddle.locationY + 10 &&
                    locationY >= paddle.locationY - 10) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /** Helper method for <code>this.checkForCollisions(Vector entities)</code>
     * @param cntrQuad The centre quad.
     * @return true if this ball collided with the centre quad, false otherwise.
     */
    public boolean collideWithCntrQuad(CentreQuad cntrQuad){
        if((locationX <= cntrQuad.locationX + cntrQuad.getWidth()/2 + 11) &&
                (locationX >= cntrQuad.locationX - cntrQuad.getWidth()/2f - 11f) &&
                (locationY <= cntrQuad.locationY + cntrQuad.getHeight()/2f + 11f) &&
                (locationY >= cntrQuad.locationY - cntrQuad.getHeight()/2f - 11f)) {
            
            ballTouchedCntrQuad = true;
            return true;
        }
        
        ballTouchedCntrQuad = false;
        return false;
    }
    
    
    /** Check for ball collision with screen border or paddles.
     * @param entities A vector of possible collision objects.
     */
    public void checkForCollisions(Vector entities) {
        // get haddles on collision candidates
        paddle = (Paddle[]) entities.get(0);
        cntrQuad = (CentreQuad) entities.get(1);
        
        
        // Check collision with up & down border and change ball direction (90 degrees).
        if(locationY <= 15) {
            speedY = 170f * normaliseFpsInterval(FPSCounter.frameInterval);
            /*lastTouched = null;
            for (int i = 0; i < paddle.length; i++) {
                paddle[i].setLastTouched(false);
            }
            */
            //locationY = (float) mode.getHeight()-8;
            
            // player 4 loses a point
            pointsCounter.setPoints(-1, 1);
            
            // play sound at this point
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[2]);
            }
            
            lastHit = System.currentTimeMillis();
            
        } else if(locationY >= mode.getHeight()-15) {
            speedY = -170f * normaliseFpsInterval(FPSCounter.frameInterval);
            /*lastTouched = null;
            //locationY = 8f;
            for (int i = 0; i < paddle.length; i++) {
                paddle[i].setLastTouched(false);
            }*/
            
            // player 2 loses a point
            pointsCounter.setPoints(-1, 3);
            
            // play sound at this point
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[2]);
            }
            
            lastHit = System.currentTimeMillis();
        }
        
        
        // If the ball collide with left or right border, change direction
        if(locationX <= 15){
            speedX = 170f * normaliseFpsInterval(FPSCounter.frameInterval);
            /*lastTouched = null;
            for (int i = 0; i < paddle.length; i++) {
                paddle[i].setLastTouched(false);
            }*/
            
            //locationX = (float) mode.getWidth()-8;
            
            //player 1 loses a point
            pointsCounter.setPoints(-1, 0);
            
            // play sound at this point
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[2]);
            }
            
            lastHit = System.currentTimeMillis();
            
        } else if(locationX >= mode.getWidth()-15) {
            speedX = -170f * normaliseFpsInterval(FPSCounter.frameInterval);
            /*lastTouched = null;
            for (int i = 0; i < paddle.length; i++) {
                paddle[i].setLastTouched(false);
            }*/
            
            //locationX = 8;
            
            //player 3 loses a point
            pointsCounter.setPoints(-1, 2);
            
            // play sound at this point
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[2]);
            }
            
            lastHit = System.currentTimeMillis();
        }
        
        
        // Check for collision with paddles :
        if(collideWithPaddle(paddle[0])) {
            // EAST paddle
            speedX = 170f * normaliseFpsInterval(FPSCounter.frameInterval);
            
            lastTouched = paddle[0];
            
            // clear out the previous last touch flags
            for(int i=0; i<paddle.length; i++){
                paddle[i].setLastTouched(false);
            }
            paddle[0].setLastTouched(true);
            
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[3]);
            }
            
            // if we waited long enough, record the time.
            lastHit = System.currentTimeMillis();
            
        } else if(collideWithPaddle(paddle[1])){
            
            //SOUTH paddle
            speedY = -170f * normaliseFpsInterval(FPSCounter.frameInterval);
            lastTouched = paddle[1];
            
            // clear out the previous last touch flags
            for(int i=0; i<paddle.length; i++){
                paddle[i].setLastTouched(false);
            }
            paddle[1].setLastTouched(true);
            
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[3]);
            }
            
            // if we waited long enough, record the time.
            lastHit = System.currentTimeMillis();
            
        } else if(collideWithPaddle(paddle[2])){
            
            // EAST paddle
            speedX = -170f * normaliseFpsInterval(FPSCounter.frameInterval);
            lastTouched = paddle[2];
            
            /// clear out the previous last touch flags
            for(int i=0; i<paddle.length; i++){
                paddle[i].setLastTouched(false);
            }
            paddle[2].setLastTouched(true);
            
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[3]);
            }
            
            // if we waited long enough, record the time.
            lastHit = System.currentTimeMillis();
            
        }else if(collideWithPaddle(paddle[3])){
            
            // NORTH paddle
            speedY = +170f * normaliseFpsInterval(FPSCounter.frameInterval);
            lastTouched = paddle[3];
            
            // clear out the previous last touch flags
            for(int i=0; i<paddle.length; i++){
                paddle[i].setLastTouched(false);
            }
            paddle[3].setLastTouched(true);
            
            if(!soundManager.isPlayingSound()){
                if (System.currentTimeMillis() - lastHit > hittingInterval)
                    soundManager.playEffect(soundsArray[3]);
            }
            
            // if we waited long enough, record the time.
            lastHit = System.currentTimeMillis();
            
        }
        
        
        
        /* Check for collisions with centre quad. The ball bounces off randomly
         * from the centre quad. The randomness is unaffected by the initial direction
         * of the ball.
         *
         */
        if(collideWithCntrQuad(cntrQuad)){
            // Check who made the last touch and update points accordingly
            if( lastTouched != null){
                
                if(!soundManager.isPlayingSound()){
                    if (System.currentTimeMillis() - lastHit > hittingInterval)
                        soundManager.playEffect(soundsArray[1]);
                }
                
                // record the hit time.
                lastHit = System.currentTimeMillis();
                
                
                if((lastTouched.equals(paddle[0]) && ballTouchedCntrQuad)){
                    // update player 1's score (left)
                    pointsCounter.setPoints(1, 0);
                    float colours[] = paddle[0].getColors();
                    cntrQuad.setColor(colours[0], colours[1], colours[2]);
                    ballTouchedCntrQuad = false;
                    
                } else if(lastTouched.equals(paddle[1]) && ballTouchedCntrQuad){
                    // update player 2 score (bottom)
                    pointsCounter.setPoints(1, 3);
                    float colours[] = paddle[1].getColors();
                    cntrQuad.setColor(colours[0], colours[1], colours[2]);
                    ballTouchedCntrQuad = false;
                    
                } else if(lastTouched.equals(paddle[2]) && ballTouchedCntrQuad){
                    // update player 3's score (right)
                    pointsCounter.setPoints(1, 2);
                    float colours[] = paddle[2].getColors();
                    cntrQuad.setColor(colours[0], colours[1], colours[2]);
                    ballTouchedCntrQuad = false;
                    
                } else if(lastTouched.equals(paddle[3]) && ballTouchedCntrQuad){
                    //update player 4's score (top)
                    pointsCounter.setPoints(1, 1);
                    float colours[] = paddle[3].getColors();
                    cntrQuad.setColor(colours[0], colours[1], colours[2]);
                    ballTouchedCntrQuad = false;
                }
            }
            //done with lastTouched for now
            lastTouched = null;
            
            for(int i=0; i<paddle.length; i++){
                paddle[i].setLastTouched(false);
            }
        }
    }
    
    
    /** Render the ball as a white quad.
     */
    
    public void render() {
        //logic();
        
        //GL11.glTranslatef(locationX, locationY, 0);
        // Set color to white.
        GL11.glColor3f(.0f, 1.0f, .0f);
        
        // Start drawing a quad.
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2i((int)(locationX - 10), (int)(locationY + 10));
            GL11.glVertex2i((int)(locationX - 10), (int)(locationY - 10));
            GL11.glVertex2i((int)(locationX + 10), (int)(locationY - 10));
            GL11.glVertex2i((int)(locationX + 10), (int)(locationY + 10));
        }
        // End quad drawing.
        GL11.glEnd();
        
        // Update ball location according to the speed.
        locationX += speedX;
        locationY += speedY;
        
        doDrawings();
    }
    
    /** Does ball specific logic
     */
    private void logic(){
    }
    
    
    /**
     *@param pc The points counter
     */
    public void setPointsCounter(PointsCounter pc){
        this.pointsCounter = pc;
    }
    
    /** Sets a handle for the game's sound manager. Useful for events that are
     * only handled by the ball.
     *
     * @param sndMngr The sound manager
     */
    public void setSoundManager(SoundManager sndMngr){
        this.soundManager = sndMngr;
    }
    
    
    /** Set sound handles which are already in the sound manager.
     * Clients of this method should remember the order of sounds should in sound
     * array, otherwise unpredictable sounds might be play -- which could lead to
     * the SoundManager.playEffect(int sound) method playing junk and possibly
     * terminating the program. So take care!
     *
     * @param sounds The int array containing sound handles for the soundManager
     */
    public void setSoundHandles(int[] sounds){
        this.soundsArray = sounds;
    }
    
    
    /**
     * Get the high resolution time in milliseconds
     *
     * @return The high resolution time in milliseconds
     */
    public long getTime() {
        // we get the "timer ticks" from the high resolution timer
        // multiply by 1000 so our end result is in milliseconds
        // then divide by the number of ticks in a second giving
        // us a nice clear time in milliseconds
        return (Sys.getTime() * 1000) / timerTicksPerSecond;
    }
    
    /** Returns whether this instance of the ball object is currently on the rebound,
     * i.e, whether it's just bounced off the wall.
     */
    /*public boolean isOnRebound(Paddle p){
        if(p.equals(paddle[0])){
            // left vertical paddle
            if(this.locationY >= p.locationY-95f && this.locationY <= p.locationY+95f){
     
                if(this.direction == 2 && this.locationX <= 20f){
                    return true;
                }
                if(this.direction == 3 && this.locationX <= 20f){
                    return true;
                }
            }
     
        } else if(p.equals(paddle[2])){
            // right vertical paddle
            if(this.locationY >= p.locationY-95f && this.locationY <= p.locationY+95f){
     
                if(this.direction == 1 && this.locationX >= mode.getWidth()-20){
                    return true;
                }
                if(this.direction == 4 && this.locationX >= mode.getWidth()-20){
                    return true;
                }
            }
     
        }else if(p.equals(paddle[1])){
            //bottom horizontal paddle
            if(this.locationX >= p.locationX-95f && this.locationX <= p.locationX+95f){
                if(this.direction == 1 && this.locationY >= mode.getHeight()-20){
                    return true;
                }
                if(this.direction == 2 && this.locationY >= mode.getHeight()-20){
                    return true;
                }
            }
     
        } else if(p.equals(paddle[3])){
            // top horizontal paddle
            if(this.locationX >= p.locationX-95f && this.locationX <= p.locationX+95f){
                if(this.direction == 3 && this.locationY <= 20f){
                    return true;
                }
                if(this.direction == 4 && this.locationY <= 20f){
                    return true;
                }
            }
        }
     
        // all tests failed
        return false;
    } */
    
    
    /** Normalises the fps values so that they don't vary too much and create
     * slow/fast movements of the ball. Normalising absically gives back a value
     * close to the average fps value.
     *
     * @param fpsInterval
     * @return the normalised fps value.
     */
    private float normaliseFpsInterval(float fpsInterval){
        if(fpsInterval < normalFpsInterval || fpsInterval > normalFpsInterval){
            return normalFpsInterval;
        }
        return fpsInterval;
    }
    
    
    /** Controlls the drawing of vector components */
    private void doDrawings(){
        // get the ball's position
        float xVectorCompOrigin, yVectorCompOrigin;
        
        // set the origin for this vector calculation
        xVectorCompOrigin = locationX;
        yVectorCompOrigin = locationY;
        
        
        if(speedY < 0 && speedX < 0){ // bottom-right -> top-left
            // change directions
            this.direction = 1;
            this.drawVectors(xVectorCompOrigin, yVectorCompOrigin, 1);
            
        } else if(speedX > 0 && speedY < 0){ //bottom-left -> top-right
            // change directions
            this.direction = 2;
            this.drawVectors(xVectorCompOrigin, yVectorCompOrigin, 2);
            
        } else if(speedX > 0 && speedY > 0){ // top-left -> down-right
            // change directions
            this.direction = 3;
            this.drawVectors(xVectorCompOrigin, yVectorCompOrigin, 3);
            
        } else if(speedX <0 && speedY >0){  // top-right -> down-left
            // change directions
            this.direction = 4;
            this.drawVectors(xVectorCompOrigin, yVectorCompOrigin, 4);
            
        }
    }
    
    /**  Draws very thin lines representing the vector components
     * @param xVectorCompOrig (float) origin for the x component
     * @param yVectorCompOrig (float) origin for the y component. Actually, has
     * the same value as the x component's. It works better separating them to prevent
     * logic errors.
     *
     * @param dir (int) A direction of the ball. Only 4 possible values, where:
     *  1: Bottom-right -> top-left
     *  2: Bottom-left -> top-right
     *  3: Top-left -> down-right
     *  4: Top-right -> down-left
     */
    private void drawVectors(float xVectorCompOrig, float yVectorCompOrig, int dir){
        /* Components are 80 pixels long, resultants are 113 pixels long */
        // TODO Add arrow heads to vector lines
        
        switch(dir){
            case 1:
            {
                //set vector coords
                int x1 = (int)xVectorCompOrig;
                int y1 = (int)yVectorCompOrig;
                int x2 = x1 - 80;
                int y2 = y1 - 80 ;
                
                // x component
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y1);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2+5, y1-5);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2+5, y1+5);
                    GL11.glEnd();
                }
                
                // the y component
                GL11.glColor3f(1.0f, .0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x1, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1-5, y2+5);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1+5, y2+5);
                    GL11.glEnd();
                }
                
                // the resultant
                GL11.glColor3f(.0f, 1.0f, .0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2-1, y2+6);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2+6, y2-2);
                    GL11.glEnd();
                }
                
            } break;
            
            case 2:
            {
                
                int x1 = (int)xVectorCompOrig;
                int y1 = (int)yVectorCompOrig;
                int x2   = x1 + 80;
                int y2 = y1 - 80;
                
                // x component
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y1);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2-5, y1-5);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2-5, y1+5);
                    GL11.glEnd();
                }
                
                // the y component
                GL11.glColor3f(1.0f, .0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x1, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1-5, y2+5);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1+5, y2+5);
                    GL11.glEnd();
                }
                
                // the resultant
                GL11.glColor3f(.0f, 1.0f, .0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2-7, y2+2);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2+2, y2+5);
                    GL11.glEnd();
                }
                
            } break;
            
            case 3:
            {
                //set vector coords
                int x1 = (int)xVectorCompOrig;
                int y1 = (int)yVectorCompOrig;
                int x2 = x1 + 80;
                int y2 = y1 + 80 ;
                
                // x component
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y1);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1-4, y2-6);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2-6, y1+4);
                    GL11.glEnd();
                }
                
                // the y component
                GL11.glColor3f(1.0f, .0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x1, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1-4, y2-6);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1+6, y2-6);
                    GL11.glEnd();
                }
                
                // the resultant
                GL11.glColor3f(.0f, 1.0f, .0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2-1, y2-6);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2-7, y2-1);
                    GL11.glEnd();
                }
            } break;
            
            case 4:
            {
                //set vector coords
                int x1 = (int)xVectorCompOrig;
                int y1 = (int)yVectorCompOrig;
                int x2 = x1 - 80;
                int y2 = y1 + 80 ;
                
                // x component
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y1);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2+4, y1-4);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y1);
                    GL11.glVertex2i(x2+4, y1+4);
                    GL11.glEnd();
                }
                
                // the y component
                GL11.glColor3f(1.0f, .04f, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x1, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1-4, y2-4);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x1, y2);
                    GL11.glVertex2i(x1+4, y2-4);
                    GL11.glEnd();
                }
                
                // the resultant
                GL11.glColor3f(.0f, 1.0f, .0f);
                GL11.glBegin(GL11.GL_LINES);
                {
                    GL11.glVertex2i(x1, y1);
                    GL11.glVertex2i(x2, y2);
                }
                GL11.glEnd();
                
                { // arrow heads
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2+1, y2-6);
                    GL11.glEnd();
                    
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2i(x2, y2);
                    GL11.glVertex2i(x2+7, y2);
                    GL11.glEnd();
                }
            } break;
         }
    }
}