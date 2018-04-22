import java.io.*;
import java.lang.*;
import java.util.*;

public class SerialR{

   public static void main(String[] args){
      Thread thread = new Thread();
      thread.start();
      Circular<String> cir = null;
      try{
         FileInputStream fileIn = new FileInputStream("circle.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         cir = (Circular<String>) in.readObject();
         in.close();
         fileIn.close();
         System.out.printf("Odczyt zakonczony pomyslnie\n");
      } catch(IOException i) {
         i.printStackTrace();
         return;
      } catch(ClassNotFoundException c) {
         System.out.println("Klasa Circular nie istnieje\n");
         c.printStackTrace();
         return;
      }

      System.out.println("Odczyt pliku serializowanego\nTest petli for : each\n");
      for(String s : cir){
         System.out.println(s);
      }

      thread.interrupt();

   }


}
