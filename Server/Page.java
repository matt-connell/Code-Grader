import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
// A Java program for a Client
import java.net.*;
import java.io.*;

public class Page extends JPanel {

    JPanel topPanel, midPanel, southPanel, namePanel, classPanel;
    JLabel label, value, classLabel, nameLabel, resultLabel;
    JTextField text;
    JButton upload;
    JTextField studentName, classFileName;
    JTextArea propertiesText,resultArea;
    JFrame properties;
    JScrollPane scrollPane;
    ArrayList<JFrame>frames = new ArrayList<>();

    public Page(){

        setLayout(new BorderLayout());
        
        //create top top panel
        //will hold text area and jlabel
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        midPanel = new JPanel();
        midPanel.setLayout(new GridLayout(4, 0));
        southPanel = new JPanel();
        add(southPanel, BorderLayout.SOUTH);

        //add(topPanel, BorderLayout.NORTH);

        label = new JLabel("Type Server IP: ");
        text = new JTextField(20);
        value = new JLabel("Not Connected", SwingConstants.CENTER);

        topPanel.add(label, BorderLayout.WEST);
        topPanel.add(text, BorderLayout.CENTER);
        topPanel.add(value, BorderLayout.SOUTH);

        
        namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        studentName = new JTextField(20);
        studentName.setColumns(30);
        nameLabel = new JLabel("Enter Name:");
        namePanel.add(studentName, BorderLayout.CENTER);
        namePanel.add(nameLabel, BorderLayout.WEST);


        classPanel = new JPanel();
        classPanel.setLayout(new BorderLayout());
        classFileName = new JTextField(20);
        classFileName.setColumns(30);
        classLabel = new JLabel("Enter File:   ");
        classPanel.add(classFileName, BorderLayout.CENTER);
        classPanel.add(classLabel, BorderLayout.WEST);

        resultLabel = new JLabel("Result: N/A", SwingConstants.CENTER);
        resultArea = new JTextArea("", 5, 20);
        resultArea.setEditable(false);
        midPanel.add(namePanel);
        midPanel.add(classPanel);
        midPanel.add(resultArea);
        createMidPanel();
        revalidate();
    
        add(midPanel);



    }


    public void addUploadListener(){
        upload.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){
                System.out.println("i have been clicked\n");
                String name = studentName.getText();
                String fileName = classFileName.getText();
                System.out.println(name.toString() + "\n" + fileName.toString());

                try{      

                    Socket s=new Socket("localhost", 8888);  
            
                    DataOutputStream dos=new DataOutputStream(s.getOutputStream());
                    
                    System.out.println("sending message to server\n");
                    value.setText("Connected to Server");

                    
                    File f = new File(fileName);
                    long length = f.length();

                    //this will write the name, length of file, filename, and then write the file 
                    dos.writeUTF(name);  
                    dos.writeLong(length);
                    dos.writeUTF(fileName); 

                    dos.flush();

                    FileInputStream fis = new FileInputStream(fileName);
                    BufferedInputStream bis = new BufferedInputStream(fis);
        
                    //this essentially reads from the file and writes the corresponding byte to the socket
                    int theByte = 0;
                    while((theByte = bis.read()) != -1) dos.write(theByte);
        
                    dos.close();  
                    
                    s.close();  


                }catch(Exception er){
                    System.out.println(er);

                }  

                //this socket will recieve the results.txt from Server and display to panel
                try{
                    Thread.sleep(12000);
                }catch(InterruptedException eee){
                    System.out.println(eee);
                }

 
                try {
                    System.out.println("waiting to recieve results...");
                    //create new socket with the same port number
                    Socket s=new Socket("localhost", 4540);  
                    System.out.println("connected...");
                    //create objects that allow to read input from socket
                    BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);  
                    String resultStr = (String) dis.readUTF();

                
                    System.out.println("contents:\n" + resultStr);
                    resultArea.setText(resultStr);
                    createResultsPanel();                    

                                
                } catch (Exception ee) {
                    //TODO: handle exception
                    System.out.println("not connected");
                    ee.printStackTrace();
                }

           
            }  
        });  
    }

    public void createMidPanel(){
        midPanel.removeAll();
        namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        studentName = new JTextField(20);
        studentName.setColumns(30);
        nameLabel = new JLabel("Enter Name:");
        namePanel.add(studentName, BorderLayout.CENTER);
        namePanel.add(nameLabel, BorderLayout.WEST);


        classPanel = new JPanel();
        classPanel.setLayout(new BorderLayout());
        classFileName = new JTextField(20);
        classFileName.setColumns(30);
        //addClassFileNameTextListener();
        classLabel = new JLabel("Enter File:   ");
        classPanel.add(classFileName, BorderLayout.CENTER);
        classPanel.add(classLabel, BorderLayout.WEST);

        upload = new JButton("Upload");
        addUploadListener();
        resultLabel = new JLabel("Result: N/A", SwingConstants.CENTER);
        //resultArea = new JTextArea("", 5, 40);

        midPanel.add(namePanel);
        midPanel.add(classPanel);
        midPanel.add(upload);
        //midPanel.add(resultArea);

        revalidate();

    }

    public void createResultsPanel(){
        midPanel.removeAll();
        midPanel.add(resultArea, SwingConstants.CENTER);

        midPanel.updateUI();
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        Page page = new Page();
        frame.add(page);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,250);
        frame.setVisible(true);
    }


}