import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.BlockingQueue;
import java.util.NoSuchElementException;
import java.lang.*;
import java.lang.Thread;
import java.lang.reflect.Array;
import java.lang.Exception;
import java.lang.Throwable;
import java.lang.InterruptedException;
import java.io.*;

public class Circular<E extends Serializable> implements Serializable, BlockingQueue<E>{

   private final E[] buf;
   private int front = 0;
   private int insertLocation = 0;
   private int size = 0;
   private int takeIndex, putIndex;

   /**
    * Funkcje pomocnicze
    */


   //Inkrementacja i dekrementacja w buforze cyklicznym
   final int inc(int i){
      return(++i == buf.length) ? 0 : i;
   }

   final int dec(int i){
      return((i == 0) ? buf.length : i) - 1;
   }

   //funkcja rzutowania na obiekt
   @SuppressWarnings("Unchecked")
   static<E> E cast(Object item){
      return (E) item;
   }

   //Zwracanie obiektu pod indeksem i
   final E itemAt(int i){
      return this.<E>cast(buf[i]);
   }

   private static void checkNotNull(Object v){
      if(v == null)
         throw new NullPointerException();
   }

   private void insert(E x){
      buf[putIndex] = x;
      putIndex = inc(putIndex);
      ++size;
   }

   private E extract(){
      final Object[] items = this.buf;
      E x = this.<E>cast(items[takeIndex]);
      items[takeIndex] = null;
      takeIndex = inc(takeIndex);
      --size;
      return x;
   }

   private void removeAt(int i){
      final Object[] items = this.buf;
      if(i == takeIndex){
         items[takeIndex] = null;
         takeIndex = inc(takeIndex);
      } else {
         for(;;){
            int nexti = inc(i);
            if(nexti != putIndex){
               items[i] = items[nexti];
               i = nexti;
            } else {
               items[i] = null;
               putIndex = i;
               break;
            }
         }
      }
      --size;
   }


   /**
    * Konstruktory
    */

   public Circular(int bsize){
      if(bsize <= 0)
         throw new IllegalArgumentException();
      this.buf = (E[]) new Serializable[bsize];
   }

   public Circular(int bsize, Collection<? extends E> c){
      this(bsize);
      int i = 0;
      try{
         for(E e : c){
            checkNotNull(e);
            buf[i++] = e;
         }
      } catch(ArrayIndexOutOfBoundsException ex){
         throw new IllegalArgumentException();
      }
      size = i;
      putIndex = (i == size) ? 0 : i;     
   }


   /**
    * Metody
    */


   public synchronized boolean add(E item){
      return offer(item);
   }

   public synchronized boolean offer(E item){
      checkNotNull(item);
      if(size == buf.length)
         return false;
      else {
         insert(item);
         return true;
      }
   }

   public synchronized boolean offer(E item, long timeout, TimeUnit unit)
     throws InterruptedException{
      checkNotNull(item);
      long millis = unit.toMillis(timeout);
      Thread.sleep(millis);
      if(size == buf.length)
         return false;
      else {
         insert(item);
         return true;
      }
   }

   
   public synchronized void put(E item) throws InterruptedException{
      checkNotNull(item);
      try{
         while(size == buf.length){
            wait();
         }
         insert(item);
      } finally {
         notify();
      }
   }

   public synchronized E poll(){
      return (size == 0) ? null : extract();
   }


   public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException{
      long millis = unit.toMillis(timeout);
      try{
         Thread.sleep(millis);
         while(size == 0){
            wait();
         }
         return (size == 0) ? null : extract();
      } finally {
         notify();
      }
   }

   public synchronized E take() throws InterruptedException{
      try{
         while(size == 0){
            wait();
         }
         return extract();
      } finally {
         notify();
      }
   }

   public synchronized E peek(){
      if(size == 0)
         throw new NoSuchElementException();
      return itemAt(takeIndex);
   }

   public synchronized E element(){
      return (size == 0) ? null : itemAt(takeIndex);
   }

   public synchronized int size(){
      return size;
   }

   public synchronized boolean isEmpty(){
      return (size > 0);
   }

   public synchronized int remainingCapacity(){
      return buf.length - size;
   }


   public synchronized E remove(){
      if(size == 0)
         throw new NoSuchElementException();
      return extract();
   }

   public synchronized boolean remove(Object o){
      if(o == null)
         return false;
      final Object[] items = this.buf;
      for(int i = takeIndex, k = size; k > 0; i = inc(i), k--){
         if(o.equals(items[i])){
            removeAt(i);
            return true;
         }
      }
      return false;
   }

   public synchronized boolean contains(Object o){
      if(o == null) 
         return false;
      final Object[] items = this.buf;
      for(int i = takeIndex, k = size; k > 0; i = inc(i), k--){
         if(o.equals(items[i]))
            return true;
      }
      return false;
   }

   @SuppressWarnings("Unchecked")
   public synchronized <T> T[] toArray(T[] arr){
      final Object[] items = this.buf;
      final int count = this.size;
      final int len = arr.length;
      if(len < count){
         arr = (T[])java.lang.reflect.Array.newInstance(
                  arr.getClass().getComponentType(), count);
         for(int i = takeIndex, k = 0; k < count; i = inc(i), k++){
            arr[k] = (T) items[i];
         }
         if(len > count){
            arr[count] = null;
         }
      }
      return arr;
   }

   public synchronized Object[] toArray(){
      final Object[] items = this.buf;
      return items;
   }


   public String toString(){
      int k = size;
      if(k == 0)
         return "[]";

      StringBuilder sb = new StringBuilder();
      sb.append('[');
      for(int i = takeIndex; ; i = inc(i)){
         Object e = buf[i];
         sb.append(e == this ? "(this Collection)" : e);
         if(--k == 0){
            return sb.append(']').toString();
         }
         sb.append(',').append(' ');
      }
   }
   
   public synchronized void clear(){
      final Object[] items = this.buf;
      for(int i = takeIndex, k = size; k > 0; i = inc(i), k--){
         items[i] = null;
      }
      size = 0;
      putIndex = 0;
      takeIndex = 0;
   }

   public synchronized int drainTo(Collection<? super E> col){
      checkNotNull(col);
      if(col == this)
         throw new IllegalArgumentException();
      final Object[] items = this.buf;
      int i = takeIndex;
      int n = 0;
      int max = size;
      while(n < max){
         col.add(this.<E>cast(items[i]));
         items[i] = null;
         i = inc(i);
         ++n;
      }
      if(n > 0){
         size = 0;
         putIndex = 0;
         takeIndex = 0;
      }
      return n;
   }

   public synchronized int drainTo(Collection<? super E> col, int maxElements){
      checkNotNull(col);
      if(col == this)
         throw new IllegalArgumentException();
      if(maxElements <= 0)
         return 0;
      final Object[] items = this.buf;
      int i = takeIndex;
      int n = 0;
      int max = (maxElements < size) ? maxElements : size;
      while(n < max){
         col.add(this.<E>cast(items[i]));
         items[i] = null;
         i = inc(i);
         ++n;
      }
      if(n > 0){
         size -= n;
         takeIndex = i;
      }
      return n;       
   }


   public synchronized boolean retainAll(Collection<?> col){
      boolean changed = false;
      for(int i = size - 1; i >= 0; i--){
         Object o = buf[i];
         if(!col.contains(o)){
            remove(o);
            changed = true;
         }
      }
      return changed;
   }

   public synchronized boolean removeAll(Collection<?> col){
      boolean changed = false;
      for(int i = size - 1; i >= 0; i--){
         Object o = buf[i];
         if(col.contains(o)){
            remove(o);
            changed = true;
         }
      }
      return changed;
   }

   public synchronized boolean addAll(Collection<? extends E> col){
      boolean changed = false;
      for(E e : col) if(add(e)) changed = true;
      return changed;
   }


   public synchronized boolean containsAll(Collection<?> col){
      Set<?> set = new HashSet<>(col);
      for(E e : this){
         if(set.contains(e)){
            set.remove(e);
            if(set.isEmpty()){
               return true;
            }
         }
      }
      return false;
   }


   public Iterator<E> iterator(){
      return new CircularIterator();
   }


   /**
    * Klasa iteratora
    */

   private class CircularIterator implements Iterator<E>{
      
      private int remaining;
      private int nextIndex;
      private E nextItem;
      private E lastItem;
      private int lastRet; //Ostatni zwrocony element

      CircularIterator(){
         lastRet = -1;
         if((remaining = size) > 0)
            nextItem = itemAt(nextIndex = takeIndex);
      }

      public boolean hasNext(){
         return remaining > 0;
      }

      public synchronized E next(){
         if(remaining <= 0)
         throw new NoSuchElementException();
         lastRet = nextIndex;
         E x = itemAt(nextIndex);
         if(x == null){
            x = nextItem;
            lastItem = null;
         } else {
            lastItem = x;
         }
         //przeskakiwanie pustych pol
         while(--remaining > 0 && (nextItem = itemAt(nextIndex = inc(nextIndex))) == null)
            ;
         return x;
      }
 
      public synchronized void remove(){
         int i = lastRet;
         if(i == -1)
            throw new IllegalStateException();
         lastRet = -1;
         E x = lastItem;
         lastItem = null;
         if(x != null && x == buf[i]){
            boolean removingHead = (i == takeIndex);
            removeAt(i);
            if(!removingHead)
               nextIndex = dec(nextIndex);
         }
      }
   }
}
