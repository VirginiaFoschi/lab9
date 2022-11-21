package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    final JLabel display = new JLabel();
    final JButton up = new JButton("up");
    final JButton down = new JButton("down");
    final JButton stop = new JButton("stop");

    private volatile boolean stopCounter;
    private int counter=0;
    private volatile boolean restartCounter;

    final Agent agent = new Agent();

    public ConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        new Thread(agent).start();
        /*
         * Register a listener that stops it
         */
        stop.addActionListener((e) -> agent.setEnabled());
        down.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                restartCounter = false;
                new Thread(agent).start();
            }
            
        });

        up.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                restartCounter = true;
                stopCounter=false;
                new Thread(agent).start();
            }
            
        });
        

    }

    private class Agent implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(!stopCounter || !restartCounter){
                try{
                    String text = Integer.toString(counter);
                    display.setText(text);
                    if(stopCounter == false) {
                        counter++;
                    }
                    else if(counter >0) {
                        counter--;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        public void setEnabled(){
            stopCounter=true;
            restartCounter = true;
        }
        
    }
}
