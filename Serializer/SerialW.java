import java.io.*;
import java.lang.*;
import java.util.*;

public class SerialW{

   public static void main(String[] args) throws IOException{
      Thread thread = new Thread();
      thread.start();
      Circular<String> cir = new Circular<String>(16);
      Scanner reader = new Scanner(System.in);
      System.out.println("Wprowadz dane [String] oddzielane enter");
      while(reader.hasNextLine()){
         String s = reader.nextLine();
         cir.add(s);
         System.out.println(cir.size());
      }
      reader.close();


      try{
         FileOutputStream fileOut = new FileOutputStream("circle.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(cir);
         out.close();
         fileOut.close();
         System.out.printf("Serializacja zapisana w circle.ser\n");
      } catch(IOException i) {
         i.printStackTrace();
      }
      thread.interrupt();

   }


}
