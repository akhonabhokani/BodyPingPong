/*
 * Copyright (c) May 4, 2006 Body PingPong Project (Meraka Institute)
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



/*
 * Bar.java
 *
 * Created on May 4, 2006, 11:22 AM
 *
 */
package za.co.meraka.components;

import org.lwjgl.opengl.DisplayMode;
import za.co.meraka.BasicGame;

/** $Id: Bar.java,v 1.1 2006/05/07 18:03:14 Ara Exp $
 *
 * A base class for (score) bars used in the game. Known subclasses
 * will represent vertical and horizontal bars.
 *
 * @author Ara
 * @version $Revision: 1.1 $
 */
public abstract class Bar {
    
    /** Flag to indicate whether this bar is visible (for rendering) */
    protected boolean visible;
    
    /** The game this ball belongs to */
    BasicGame game;
    
    /** Display mode needed to get screen bounderies */
    protected DisplayMode mode;
    
    /** Location on X axis */
    public float locationX;
    
    /** Location on Y axis. **/
    public float locationY;

    //~--- methods ------------------------------------------------------------

    /** Render this bar as a white quad. */
    public abstract void render();
    
    /** Sets the visible to either true or false. */
    public void setVisible(boolean visisble){
        this.visible = visible;
    }
    
    /** Tests whether this bar is visible or not. */
    public boolean isVisible(){
        return this.visible;
    }
    
    /** Sets location for this bar. */
    public void setLocation(float x, float y){
        locationX = x;
        locationY = y;
    }
}

//~ Formatted by Jindent --- http://www.jindent.com
