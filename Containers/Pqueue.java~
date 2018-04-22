
//Bez stosowania java.util.PriorityQueue


public class Pqueue<T> extends Object{

   class Pair<T, Integer> {
       public T first;
       public int second;

       public Pair(T first, int second){
          this.first = first;
          this.second = second;
       }
   }

   Pair[] tab;
   private int size;


   public Pqueue(){
      tab = new Pair[20];
      size = 0;
   }

   public void add(T t, int priority){
      tab[++size] = new Pair(t, priority);
      heapifyUp(size);
   }

   //Funkcje sortowania w stosie
   public void heapifyUp(int k){
      while(k > 1 && more(k/2, k)){
         swap(k/2, k);
         k = k/2;
      }
   }

   public void heapifyDown(int k){
      while(2*k < size){
         int j = 2 * k;
         if(j < size && more(j, j+1)) j = j + 1;
         if(more(j, k)) break;
         swap(k, j);
         k = j;
      }
   }

   private boolean more(int i, int j){
      if(tab[i].second > tab[j].second)
         return true;
      return false;
   }


   public Object get(){
      if(isEmpty()) return null;
      Object o = tab[1].first;
      swap(1, size--);
      tab[size+1] = null;
      heapifyDown(1);

      return o;
   }

   private void swap(int i, int j){
      Pair<T, Integer> temp = tab[i];
      tab[i] = tab[j];
      tab[j] = temp;
   }

   private boolean isEmpty(){
      return size == 0;
   }


   public static void main(String[] args){

      Pqueue<String> kolejka = new Pqueue<>();
      kolejka.add("kota", 3);
      kolejka.add("Ala", 1);
      kolejka.add("ma", 7);
      kolejka.add("ma", 2);
      kolejka.add("a", 5);
      kolejka.add("Ale", 8);
      kolejka.add("kot", 6);
      kolejka.add(", ", 4);

      for(int i = 0; i < 8; i++){
         System.out.print(kolejka.get() + " ");
      }
      System.out.println();
   }
}
