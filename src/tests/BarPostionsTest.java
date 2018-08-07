/*
 * BarPostionsTest.java
 *
 * Created on May 11, 2006, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager and locate the
 * template. Click the Open in Editor button.
 * You can then make changes to the template in the Source Editor.
 */

package tests;

import za.co.meraka.components.BarPositions;

/** $Id: BarPostionsTest.java, v 1.0 May 11, 2006 11:43 AM  Akhona Exp $
 *
 * @author Akhona
 * @version $Revision: 1.0 $
 */
public class BarPostionsTest {
    
    /** Creates a new instance of BarPostionsTest */
    public BarPostionsTest() {
    }
    
    public static void main(String[] args) {
      System.out.println("Testing class za.co.meraka.components.BarPositions");
      
      assert BarPositions.HORIZONTAL_BAR_POSITIONS_START_X1 != 5;
      
      assert BarPositions.HORIZONTAL_BAR_POSITIONS_START_X2 == 1019;
      
      assert BarPositions.VERTICAL_BAR_POSITIONS_START_Y1 == 5;
      
      assert BarPositions.VERTICAL_BAR_POSITIONS_START_Y2 == 763;
      
      short hBarPosStartY = 300, vBarPosStartX = 300;

      assert BarPositions.horizontalBarYpositions[0] == hBarPosStartY;
      assert BarPositions.verticalBarXpositions[0] == vBarPosStartX;

      for(int i=1; i<60; i++){
          short gap = 3;//pixels
          
          assert BarPositions.horizontalBarYpositions[i] ==
                  BarPositions.horizontalBarYpositions[i-1] + gap;
          
          assert BarPositions.verticalBarXpositions[i] == 
                  BarPositions.verticalBarXpositions[i-1] + gap;
      }
      
      System.out.println("Finished testing for class za.co.meraka.components.BarPositions");
    }
}
