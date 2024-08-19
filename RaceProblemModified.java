/**
 * This program is used to show a buffer that uses semaphore functionality. This experiment occurs until a race condition occurs between the producer 
 * and consumer.
 *
 * CSCI 331
 * 
 * Kylie Heiland
 * 
 * 10/10/23
 */
import java.util.concurrent.Semaphore;

public class RaceProblemModified
{
    static final int BUFFER_SIZE = 10; //Buffer size is 10.
    static int []buffer = new int[BUFFER_SIZE];
    static final int ROUND = 20; //The default run is 20.
    static int limit = ROUND; //When limit = 0, program ends.
    static int next_in = 0, next_out = 0; //next_in shows slot to be filled next, next_out shows slot to be emptied next.
    static int count = 0; //Count how many rounds no race condition.
    static Semaphore emptyBuffer = new Semaphore(buffer.length); //Since at the beginning, the buffer is empty, emptyBuffer equals how many slots are in buffer.
    static Semaphore occupiedBuffer = new Semaphore(0); //Since at the beginning, the buffer is empty, then there is no occupied slots in the buffer.
    //static boolean done = false; //Controls last round.
    
    
    public static void main(String[] args) throws InterruptedException{
         //Creates producer thread.
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    producer();
                } catch (InterruptedException e){
                        e.printStackTrace();
                }
            }
        });
    
        //Creates consumer thread.
        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    consumer();
                } catch (InterruptedException e){
                        e.printStackTrace();
                }
            }
        });
        
        //Start both threads.
        t1.start();
        t2.start();
        
        //Joins both threads.
        t1.join();
        t2.join();      
    }
    
    public static void producer() throws InterruptedException{
        while(true){ //Loops through the rounds.
            //Gets random number between 1 and half of buffer size.
            int k1 = (int)(Math.random() * buffer.length/2) + 1;
            
            //Executes short burst that is the duration of k1. 
            for(int i = 0; i < k1; i++){
                emptyBuffer.acquire(); //Acquires one permit from emptyBuffer.
                //During the short burst, next_in is used to determine the index that will have 1 added to it.
                //Assuming next_in starts at index 0, then 1 is added to this particular buffer index. Then, when i = 1, the buffer[1] has 1 added to it. 
                //This continues adding 1 to the buffer index, where buffer's index is: next_in + (i == k1 - 1). 
                buffer[(next_in + i) % buffer.length] += 1;
                occupiedBuffer.release(); //Releases one permit from occupiedBuffer.
            }
            //Since we have filled the buffer from buffer[next_in] until buffer[next_in + (k1 - 1)] 
            //(this is assuming we have not reached the end of the buffer while filling; else buffer would have started back at index 0 until i reached k1),
            //then next_in is now set to the now next empty spot. 
            next_in = (next_in + k1) % buffer.length;
            System.out.println("So far producer OK " + count++);
            limit--;
            
            if(limit <= 0){
                System.out.println("Producer exits system without any race problem");
                System.exit(1); //Once producer is done, it forces consumer to be done too.
            }
            //Sleep periodically for random time interval, to emulate unpredictable execution speeds.
            Thread.sleep((int)(Math.random() * 1000));
        }
    }
    
    public static void consumer() throws InterruptedException{
        while(true){
            //Sleep periodically for random time interval, to emulate unpredictable execution speeds.
            Thread.sleep((int)(Math.random() * 1000)); 
            //Gets random number.
            int k2 = (int)(Math.random() * buffer.length/3) + 1;
            
            //Executes short burst that is the duration of k1. 
            for(int i = 0; i < k2; i++){
                occupiedBuffer.acquire();
            
                //Gets the data from the buffer, starting at index 0 (that is assuming next_out starts at 0), and looping through the indexes until i = k2.
                int data = buffer[(next_out + i) % buffer.length];
                if(data > 1){ //If the data is greater than 1, then that means the producer is faster than the consumer.
                    System.out.println("Race condition detected! Consumer too slow");
                    System.exit(1);                    
                } else if(data == 0){ //If the data is 0, then the consumer is faster than the producer.
                    System.out.println("Race condition detected! Producer too slow");
                    System.exit(1); 
                } else buffer[(next_out + i) % buffer.length] = 0; //If both producer and consumer caused no race condition to occur.
                
                emptyBuffer.release();
            }
            //Since we have emptied the buffer from buffer[next_in] until buffer[next_in + (k2 - 1)] 
            //(this is assuming we have not reached the end of the buffer while filling; else buffer index would have started back at 0 until i reached k1),
            //then next_out is now set to the next buffer slot that needs to be emptied. 
            next_out = (next_out + k2) % buffer.length;
            System.out.println("So far consumer OK " + count++);
            limit--;
            
            if(limit <= 0){
                System.out.println("Consumer exits system without any race problem");
                System.exit(1); //Once consumer is done, it forces producer to be done too.
            }
            //Sleep periodically for random time interval, to emulate unpredictable execution speeds.
            Thread.sleep((int)(Math.random() * 1000));
        }
    }
}
/*OUTPUT
So far producer OK 0
So far producer OK 1
So far consumer OK 2
So far producer OK 3
So far consumer OK 4
So far producer OK 5
So far producer OK 6
So far consumer OK 7
So far consumer OK 8
So far producer OK 9
So far consumer OK 10
So far producer OK 11
So far consumer OK 12
So far producer OK 13
So far consumer OK 14
So far consumer OK 15
So far producer OK 16
So far consumer OK 17
So far consumer OK 18
So far producer OK 19
Producer exits system without any race problem
 

So far producer OK 0
So far consumer OK 1
So far producer OK 2
So far producer OK 3
So far consumer OK 4
So far producer OK 5
So far producer OK 6
So far producer OK 7
So far consumer OK 8
So far producer OK 10
So far consumer OK 9
So far consumer OK 11
So far producer OK 12
So far consumer OK 13
So far producer OK 15
So far consumer OK 14
So far consumer OK 16
So far producer OK 17
So far consumer OK 18
So far consumer OK 19
Consumer exits system without any race problem


So far producer OK 0
So far producer OK 1
So far consumer OK 2
So far producer OK 3
So far consumer OK 4
So far producer OK 5
So far consumer OK 6
So far producer OK 7
So far consumer OK 8
So far producer OK 9
So far producer OK 10
So far consumer OK 11
So far consumer OK 12
So far producer OK 13
So far consumer OK 14
So far producer OK 15
So far consumer OK 16
So far consumer OK 17
So far producer OK 18
So far consumer OK 19
Consumer exits system without any race problem 
 * 
 * 
 * 
 */
