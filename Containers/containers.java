import java.util.*;

public class containers{


public static void main(String[] args){

   //Array List
   ArrayList<Integer> array = new ArrayList<Integer>();

   long start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      array.add(i);
   }
   long est = System.nanoTime() - start;
   System.out.println("Array add: " + (est/10));

   start = System.nanoTime();
   boolean flag = array.contains(5);
   est = System.nanoTime() - start;
   System.out.println("Array contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      array.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Array remove: " + (est/10) + "\n");


   //Hash Set
   Set<Integer> set = new HashSet<Integer>();
   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      set.add(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Set add: " + (est/10));

   start = System.nanoTime();
   flag = set.contains(5);
   est = System.nanoTime() - start;
   System.out.println("Set contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      set.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Set remove: " + (est/10) + "\n");


   //Linked List
   Queue<Integer> lista = new LinkedList<Integer>();

   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      lista.add(i);
   }
   est = System.nanoTime() - start;
   System.out.println("List add: " + (est/10));

   start = System.nanoTime();
   flag = lista.contains(5);
   est = System.nanoTime() - start;
   System.out.println("List contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      lista.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("List remove: " + (est/10) + "\n");

   
   //Stack
   Stack<Integer> stos = new Stack<Integer>();

   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      stos.push(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Stack add: " + (est/10));

   start = System.nanoTime();
   stos.search(5);
   est = System.nanoTime() - start;
   System.out.println("Stack contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      stos.pop();
   }
   est = System.nanoTime() - start;
   System.out.println("Stack remove: " + (est/10) + "\n");


   //Wektor
   Vector<Integer> vec = new Vector<Integer>();

   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      vec.addElement(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Vector add: " + (est/10));

   start = System.nanoTime();
   flag = vec.contains(5);
   est = System.nanoTime() - start;
   System.out.println("Vector contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      vec.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Vector remove: " + (est/10) + "\n");


   //PriorityQueue
   PriorityQueue<Integer> pq = new PriorityQueue<Integer>();

   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      pq.add(i);
   }
   est = System.nanoTime() - start;
   System.out.println("PQueue add: " + (est/10));

   start = System.nanoTime();
   flag = pq.contains(5000);
   est = System.nanoTime() - start;
   System.out.println("PQueue contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      pq.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("PQueue remove: " + (est/10) + "\n");


   //TreeSet
   TreeSet<Integer> tree = new TreeSet<Integer>();

   start = System.nanoTime();
   for(int i = 0; i < 10000; i++){
      tree.add(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Tree add: " + (est/10));

   start = System.nanoTime();
   flag = tree.contains(5);
   est = System.nanoTime() - start;
   System.out.println("Tree contains: " + est);

   start = System.nanoTime();
   for(int i = 9999; i >= 0; i--){
      tree.remove(i);
   }
   est = System.nanoTime() - start;
   System.out.println("Tree remove: " + (est/10) + "\n");

}
}
