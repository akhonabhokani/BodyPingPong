/*
 * TestControllers.java
 *
 * Created on March 13, 2006, 10:14 PM
 *
 *
 */

package tests;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * $Id: TestControllers.java,v 1.2 2006/03/17 14:16:36 Compaq P4-1.8GHz Exp $
 *
 *
 *
 * @author Akhona
 * @version $Revision: 1.2 $
 */


public class TestControllers extends JPanel {
    public static int total = 0;
    
    private JTextField[] values;
    private JTextField[] names;
    private Controller controller;
    private int buttonCount;
    private int itemCount;
    
    public TestControllers(int index) {
        controller = Controllers.getController(index);
        setLayout(null);
        
        buttonCount = controller.getButtonCount()-6;
        itemCount = controller.getButtonCount() + controller.getAxisCount();// + 2;
        values = new JTextField[itemCount];
        names = new JTextField[itemCount];
        
        for (int i=0; i<buttonCount; i++) {
            names[i] = new JTextField();
            names[i].setEditable(false);
            names[i].setBounds(0,i*30,100,30);
            names[i].setText(controller.getButtonName(i));
            add(names[i]);
            values[i] = new JTextField();
            values[i].setEditable(false);
            values[i].setBounds(100,i*30,100,30);
            add(values[i]);
        }
        
        for (int i=buttonCount; i<buttonCount + controller.getAxisCount();i++) {
            names[i] = new JTextField();
            names[i].setEditable(false);
            names[i].setBounds(0,i*30,100,30);
            names[i].setText(controller.getAxisName(i-buttonCount));
            add(names[i]);
            values[i] = new JTextField();
            values[i].setEditable(false);
            values[i].setBounds(100,i*30,100,30);
            add(values[i]);
        }
        
        
        total++;
        
        setPreferredSize(new Dimension(200,30*itemCount));
        JFrame frame = new JFrame(controller.getName());
        frame.setContentPane(new JScrollPane(this));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                total--;
                if (total == 0) {
                    System.exit(0);
                }
            }
        });
        frame.setSize(230,450);
        frame.setLocation(index*30,index*30);
        frame.setVisible(true);
        
        Thread t = new Thread() {
            public void run() {
                while (true) {
                    try { Thread.sleep(10); } catch (Exception e) {};
                    pollAndUpdate();
                }
            }
        };
        t.start();
    }
    
    public void pollAndUpdate() {
        Controllers.poll();
               
        for (int i=0; i<buttonCount; i++) {
            values[i].setText(""+controller.isButtonPressed(i));
        }
        for (int i=buttonCount;i<buttonCount+controller.getAxisCount();i++) {
            values[i].setText(""+controller.getAxisValue(i-buttonCount));
        }
        
        while (Controllers.next()) {
            System.out.println("Event Fired: ");
            System.out.println("\tIs event X-Axis: "+Controllers.isEventXAxis());
            System.out.println("\tIs event Y-Axis: "+Controllers.isEventYAxis());
        }
    }
    
    public static void main(String[] argv) {
        try {
            Controllers.create();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        int count = Controllers.getControllerCount();
        System.out.println(count+" Controllers Found");
        for (int i=0;i<count;i++) {
            Controller controller = Controllers.getController(i);
            System.out.println(controller.getName());
        }
        
        if (count == 0) {
            System.exit(0);
        }
        
        for (int i=0;i<count;i++) {
            new TestControllers(i);
        }
    }
}
