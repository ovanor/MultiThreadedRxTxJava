
package com.test.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
/*
 *  
*  
 *  Jrxtx library in combination with native libraries provide the required interfaces for serial communication. 
 *  For More details
*  
 *   @see <a href="https://github.com/openmuc/jrxtx"> Java serial Communication library</a>
*  
 *  
 */

public class RxtxSampleMultithreading {

       private static Logger log = LoggerFactory.getLogger(RxtxSampleMultithreading.class);
   
       private static String inCommand = "inputCommands";

       @SuppressWarnings("deprecation")
       void connectSerialPort(String portName) throws Exception {
              CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
              if (portIdentifier.isCurrentlyOwned()) {
                     System.out.println("Error: Port is currently in use");

              } else {
                     int timeout = 2000;
                     CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);

                     if (commPort instanceof SerialPort) {
                           SerialPort serialPort = (SerialPort) commPort;

                           InputStream in = serialPort.getInputStream();
                           OutputStream out = serialPort.getOutputStream();

                           (new Thread(new SerialPortStreamReader(in))).start();
                           (new Thread(new SerialPortStreamWriter(out))).start();

                           in.close();
                           out.close();

                     } else {
                           System.out.println("Not a serial port");
                     }
              }
       }

       public static class SerialPortStreamReader implements Runnable {

              InputStream in;

              public SerialPortStreamReader(InputStream in) {
                     this.in = in;
              }

              public void run() {
                     System.out.println("Inside Serial Reader");
                     byte[] buffer = new byte[1024];
                     int len = -1;
                     try {
                           while (true) {
                                  if ((len = this.in.read(buffer)) > -1) {
                                         System.out.print(new String(buffer, 0, len));
                                  }
                                  Thread.currentThread().sleep(1000);
                           }
                     } catch (IOException e) {
                           e.printStackTrace();
                     } catch (InterruptedException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                     }
              }
       }

       public static class SerialPortStreamWriter implements Runnable {

              OutputStream out;

              public SerialPortStreamWriter(OutputStream out) {
                     this.out = out;
              }

              public void run() {
                     try {
                           System.out.print("Inside Serial Writer");
                           while(true) {
                           this.out.write(inCommand.getBytes());
                           this.out.flush();
                           Thread.currentThread().sleep(2000);
                           }
                     } catch (IOException e) {
                           e.printStackTrace();
                     } catch (InterruptedException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                     }
              }
       }

       public static void main(String[] args) {
              try {

                     (new RxtxSampleMultithreading()).connectSerialPort("COM1");
					 (new RxtxSampleMultithreading()).connectSerialPort("COM2");
					 (new RxtxSampleMultithreading()).connectSerialPort("COM3");

              } catch (Exception e) {
                     e.printStackTrace();
              }
       }
}
