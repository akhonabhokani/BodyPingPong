/*
 * Copyright (c) May 8, 2006 Body PingPong Project (Meraka Institute)
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
 * * Neither the name of 'Body PingPong' nor the names of
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
 * BarPositions.java
 *
 * Created on May 8, 2006, 2:46 PM
 */
package za.co.meraka.components;

/** $Id: BarPositions.java, v 1.0 May 8, 2006 2:46 PM  Ara Exp $
 *
 * This class contains predefind positions for point bars.
 * A purely static singleton class.
 *
 * @author Ara
 * @version $Revision: 1.0 $
 */
public class BarPositions {

    // vertical and horizontal bars and NOT paddles.

    /**
     * The left starting position (X) for horizontal bars.
     */
    public final static short HORIZONTAL_BAR_POSITIONS_START_X1 = 5;

    /**
     * The right starting position (X) for horizontal bars.
     */
    public final static short HORIZONTAL_BAR_POSITIONS_START_X2 = 1019;//1024-5
    
    /**
     * The north starting positions (Y) for vertical bars.
     */
    public final static short VERTICAL_BAR_POSITIONS_START_Y1 = 5;
    
    /**
     * The south starting positions (Y) for vertical bars.
     */
    public final static short VERTICAL_BAR_POSITIONS_START_Y2 = 763; //768-5
    
    /**
     * The starting position (Y) for horizontal bars. Accessibility: Private
     */
    private static short horizontalBarPositionsStartY = 230;

    /**
     * The starting position (X) for vertical bars. Accessibility: Private
     */
    private static short verticalBarPositionsStartX = 320;

        
    /* Vertical bars need only keep an array for their X positions and
     * horizontal bars need only keep an array for their Y positions.
     * |---------------------------------------------------------|
     * |                        |||||||||                        |
     * |-                                                       -|
     * |-                                                       -|
     * |-                      Playing area                     -|
     * |-                                                       -|
     * |                        |||||||||                        |
     * |_________________________________________________________|
     *
     * (-,|) represent bars
     */
    
    public static short[]       horizontalBarYpositions    = new short[60];
    public static short[]       verticalBarXpositions      = new short[60];
    

    /**
     * The gap between bars.
     */
    private static short gap = 5;    // 3 pixels
    
    //~--- static initializers ------------------------------------------------

//    public static void positionBars(){
    static{
        horizontalBarYpositions[0] = horizontalBarPositionsStartY;
        verticalBarXpositions[0]   = verticalBarPositionsStartX;
        
        // Position the first set of horizontal bars (left set)--
        for (int i = 1; i < 30; i++) {
            horizontalBarYpositions[i] =
                (short)(horizontalBarYpositions[i - 1] + gap);
        }
        
        // Position the second set of horizontal bars (right set)--
        for (int i = 30; i < 60; i++) {
            horizontalBarYpositions[i] =
                    (short)(horizontalBarYpositions[i-1] + gap);
        }
        
        //~--- Position vertical bars ---------------------
       
        // Position first set of horizontal bars
        for (int i = 1; i < 30; i++) {
            verticalBarXpositions[i] = 
                    (short)(verticalBarXpositions[i-1] + gap);
        }
        
        // Position first set of horizontal bars
        for (int i = 30; i < 60; i++) {
            verticalBarXpositions[i] = 
                    (short)(verticalBarXpositions[i-1] + gap);
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
