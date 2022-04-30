// A Java program for a Server
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import java.io.*;
import java.lang.*;
import java.awt.event.*;

import javax.swing.JFrame;

public class Server{
        
    public static void main(String[] args) throws IOException{

 
    
        //this Socket is for passing the Page.class and its handlers to the Client Program
        ServerSocket ss1 = new ServerSocket(2310);
        ServerSocket ss2 = new ServerSocket(8888);
        ServerSocket ss3 = new ServerSocket(4540);

        while(true){
        String[] names = new String[3];
        names[0] = "Page$2.class";
        names[1] = "Page$1.class";
        names[2] = "Page.class";

        try {

            
            System.out.println("waiting to connect for Page.class");

            //create file and set port number to 2310        
            Socket socket = ss1.accept();
        
            //once socekt is accepted, we are now connected
            System.out.println("connected");
        
            //create bufferedoutputsream object which allows us to write to the output
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        
            //by using dataoutputstream object this allows us to send specific data types
            DataOutputStream dos = new DataOutputStream(bos);
        
            //we first write the number of files we are passing

    
            //loop through 
            for (int i = 0; i < names.length; i++){
                    
                File f = new File(names[i]);
                long length = f.length();
                    
                dos.writeLong(length); //writes the size of file (long)
                dos.writeUTF(names[i]); //writes the name of file (string)
        
                System.out.println("filename: " + names[i]);
                System.out.println("filelength: " + length);
                System.out.println("________________\n");
        
                //this process allows us to pass the file contents
                FileInputStream fis = new FileInputStream(names[i]);
                BufferedInputStream bis = new BufferedInputStream(fis);
        
                //this essentially reads from the file and writes the corresponding byte to the socket
                int theByte = 0;
                while((theByte = bis.read()) != -1) bos.write(theByte);
        
                dos.flush();
                bis.close();
            }
        
            dos.close();
            
        } catch (Exception e) {
            //TODO: handle exception
        }
    
        //this socket is for recieving a C Program from the Client
        String resultString = "";
        String name = "";
        long size = 0;
        String file = "";

        try{  

            System.out.println("waiting to connect to the Server for revieving a C Program");

            //Socket that revieves a name, length of file, and a file from the Client
             
            Socket s=ss2.accept();//establishes connection 
            System.out.println("connected...\n");

            BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
            DataInputStream dis = new DataInputStream(bis);  

            //this will read the name, length of file, and file name, followed by the file
            name =(String)dis.readUTF();  
            size = (long)dis.readLong();
            file = (String)dis.readUTF();

            File programSub;
            try {
                File newStudentDir = new File(name);
                newStudentDir.mkdir();

                String programName = file;
                programSub = new File(newStudentDir, programName);
                programSub.createNewFile();

                //streams to read file into student directory
                FileOutputStream fos = new FileOutputStream(programSub);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                
                //streams to read into main directory
                FileOutputStream fos2 = new FileOutputStream(file);
                BufferedOutputStream bos2 = new BufferedOutputStream(fos2);

                int result = bis.read();
                while(result != -1) {
                    //writes to student
                    bos.write((byte) result);
                    //writes to main
                    bos2.write((byte) result);

                    //get next byte
                    result = bis.read();
                }
            
                bos.close();
                bos2.close();
            } catch (Exception e) {
                //TODO: handle exception
                System.out.println("error creating directories");
            }
            
            System.out.println("student name " + name);  
            System.out.println("length " + size); 
            System.out.println("class name " + file); 

        
            File fd = new File(args[0]);
            Scanner fileReader = new Scanner(fd);
            ArrayList<Integer> values = new ArrayList<Integer>();


            boolean isInput = true;
            int count = 0;

            //create new process, with filename
            Process p = Runtime.getRuntime().exec("gcc " + file); 
            try {
                //waits for process
                TimeUnit.SECONDS.sleep(5);
            
            } catch (Exception e) {
                //TODO: handle exception
            }
           
            int exitValue = p.exitValue();
            if (exitValue != 0){
                System.out.println(exitValue);
                resultString += "Failed to Compile.\n";
            } else {
                p = Runtime.getRuntime().exec("./a.out");
                resultString += "Compiled Successfully.\n";

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream())); 
                OutputStream out = p.getOutputStream(); 
    
    
                int outputResult = 0;
                int expectedResult = 0;
                int counter = 1;
                while(fileReader.hasNextLine()){
            
                    String line = fileReader.nextLine();
    
                    if (!line.equals("*") && isInput){
                        System.out.println("sending...");
                        writeToProc(out, line + "\n");
    
                    //this is where the input values stop
                    } else if (line.equals("*")){
                        isInput = false; //set to false since we will not be accepting input
                        String curr = null;  
                        String str = ""; 
                        while ((curr = in.readLine()) != null) {   
                            str += curr;
                            System.out.println(curr);   
                        }
                        outputResult = Integer.parseInt(str);
                        System.out.println("output is : " + str);
    
                    } else if (!isInput && !line.equals("*") && !line.equals("#")){
                        expectedResult = Integer.parseInt(line);
    
                        System.out.println("expected result is : " + expectedResult);
    
                        if(expectedResult == outputResult){
                            System.out.println("pass");
                            if (counter == 1)
                                resultString += "test1 : pass\n";
                            else
                                resultString += "test2 : pass\n";

                            

                        } else {
                            System.out.println("fail");
                            if (counter == 1)
                                resultString += "test1 : fail\n";
                            else
                                resultString += "test2 : fail\n";

                        }
    
                    } else if (line.equals("#") && fileReader.hasNextLine()){
    
    
                        //we will now be starting new test case
                        isInput = true;
                        counter++;

                        values.clear();
                        p = Runtime.getRuntime().exec("gcc " + file); 
                        try {
                            //waits for process
                            TimeUnit.SECONDS.sleep(5);
                        
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                        //run the executable file
                        p = Runtime.getRuntime().exec("./a.out");
            
                        
                        //reads in the output from the compiled file
                        in = new BufferedReader(new InputStreamReader(p.getInputStream())); 
                        out = p.getOutputStream();   
    
    
                    }

                }
            }
            
            

        }catch(Exception er){
            System.out.println(er);
            er.printStackTrace();
        } 
        
        
        
        //moving results to student directory
        try {
            File newStudentDir = new File(name);
            newStudentDir.mkdir();

            //creating the report file for student directory
            String reportName = "report.txt";
            File reportFile = new File(newStudentDir, reportName);
            reportFile.createNewFile();
            FileWriter reportWriter = new FileWriter(reportFile); 
            reportWriter.write(resultString);
            reportWriter.close();

            //adding the test code to student directory

            //deletes the c file from server directory
            File f = new File(file);
            f.delete();
            File a = new File("a.out");
            a.delete();
  
        } catch (Exception eerr) {
        //TODO: handle exception
            System.out.println("error moving directories");
        }
        
        
        
        //this socket will be for sending the results to the Client Program to be displayed
        try {
            
            System.out.println("waiting to connect to send results.txt");

            //create file and set port number to 2310
            Socket socket = ss3.accept();
    
            //once socekt is accepted, we are now connected
            System.out.println("connected");
    
            //create bufferedoutputsream object which allows us to write to the output
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
    
            //by using dataoutputstream object this allows us to send specific data types
            DataOutputStream dos = new DataOutputStream(bos);
            

            dos.writeUTF(resultString);
            dos.flush();


        } catch (Exception e) {
            //TODO: handle exception
        }
    }
        
    }


    public static void writeToProc(OutputStream out, String msg) throws IOException {
        // <change>
        // Using UTF-8 encoding since all chars in C are byte sized
        byte[] buff = msg.getBytes("UTF-8");
        out.write(buff);
        out.flush();
        System.out.println("done writing: " + new String(buff));
    }

    public static void runTestCases(OutputStream out, String fileName){
        try {
            File fd = new File(fileName);
            Scanner fileReader = new Scanner(fd);
            ArrayList<Integer> values = new ArrayList<Integer>();

            boolean isInput = true;
            while(fileReader.hasNextLine()){
        
                String line = fileReader.nextLine();


                if (!line.equals("*") && isInput){
                    values.add(Integer.parseInt(line));
                    writeToProc(out, line + "\n");
                    System.out.println(values.toString());
                
                } else if (line.equals("*")){
                    //we will now be entering the expected output
                    isInput = false;
                } else if (line.equals("#")){
                    //we will now be starting new test case
                    isInput = true;
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("error");
        }
    }

}


