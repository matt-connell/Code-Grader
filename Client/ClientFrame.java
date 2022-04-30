import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Class;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.ArrayList;

// A Java program for a Client
import java.net.*;
import java.io.*;

public class ClientFrame extends JFrame {

    JPanel appPanel;
    JPanel topPanel;
    JTextField text;
    JLabel label;

    public ClientFrame(){

        super("Applet");

        setLayout(new BorderLayout());
        appPanel = new JPanel();
        appPanel.setLayout(new BorderLayout());
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        text = new JTextField(20);
        label = new JLabel("Server IP: ");
        topPanel.add(text, BorderLayout.CENTER);
        topPanel.add(label, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        add(appPanel, BorderLayout.CENTER);
 
            //add key listener to the text area
        text.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e) {
                //will remove the properties frames
    
    
                    //listens for 'enter' key
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        
                        //create new socket with the same port number
                        Socket s=new Socket("localhost", 2310);  
            
                        //create objects that allow to read input from socket
                        BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
                        DataInputStream dis = new DataInputStream(bis);  
            
            
                        File[]files = new File[3];
            
            
                        long size;
                        String argName;
                                    
                        //loop through the number of files passed
                        for(int i = 0; i < 3; i++){
            
                            //reads long data type
                            size = dis.readLong();                            
            
                            //reads string, name of file
                            argName = dis.readUTF();
                            //add to array of files passed
                            files[i] = new File(argName);
            
                            System.out.println("filename: " + files[i].toString());
                            System.out.println("filelength: " + size);
                            System.out.println("________________\n");
            
                            //this allows the client to recieve a file that was passed to it and writes to the file
                            FileOutputStream fos;
                            if (i == 0)
                                fos = new FileOutputStream("Page$2.class");
                            else if (i == 1)
                                fos = new FileOutputStream("Page$1.class");
                            else 
                                fos = new FileOutputStream("Page.class");


                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                                    
                            for(int j = 0; j < size; j++) bos.write(bis.read());
            
                            bos.close();
                                        
            
                        }
                        
            
                     
                        appPanel.removeAll();
                        Class<?> c = Class.forName("Page");
                        Constructor<?> con = c.getConstructor();
            
                        appPanel.add((Component) con.newInstance(), BorderLayout.CENTER);
                        revalidate();
            
            
              
                        bis.close();
            
                    } catch (Exception error) {
                        System.out.println("this ain't it chief");
                    }
                
                               
    
                } 
            }
                
    
            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
            }
    
            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
            }
        });
    
    }

    public String stripClassName(String name){
        //this takes a string and will remove the .class extension from the string
        int size = name.length();
        String className = name.substring(0, size-6);
        return className;
    }
}


