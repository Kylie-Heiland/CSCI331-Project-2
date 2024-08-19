/**
 * This program is used to show a buffer that does not use semaphore functionality. This experiment occurs until a race condition occurs between the producer 
 * and consumer.
 *
 * CSCI 331
 * 
 * Kylie Heiland
 * 
 * 10/10/23
 */
public class RaceProblem
{
    static int []buffer = new int[100]; //Buffer is large array of 100 integers.
    static int next_in = 0, next_out = 0; //next_in shows slot to be filled next, next_out shows slot to be emptied next.
    static int count = 0; //Count how many rounds no race condition.
    
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
        while(true){
            //Gets random number.
            int k1 = (int)(Math.random() * buffer.length/3) + 1;
            
            //Executes short burst that is the duration of k1. 
            for(int i = 0; i < k1; i++){
                //During the short burst, next_in is used to determine the index that will have 1 added to it.
                //Assuming next_in starts at index 0, then 1 is added to this particular buffer index. Then, when i = 1, the buffer[1] has 1 added to it. 
                //This continues adding 1 to the buffer index, where buffer's index is: next_in + (i == k1 - 1). 
                buffer[(next_in + i) % buffer.length] += 1;
            }
            //Since we have filled the buffer from buffer[next_in] until buffer[next_in + (k1 - 1)] 
            //(this is assuming we have not reached the end of the buffer while filling; else buffer would have started back at index 0 until i reached k1),
            //then next_in is now set to the now next empty spot. 
            next_in = (next_in + k1) % buffer.length;
            
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
                //Gets the data from the buffer, starting at index 0 (that is assuming next_out starts at 0), and looping through the indexes until i = k2.
                int data = buffer[(next_out + i) % buffer.length];
                if(data > 1){ //If the data is greater than 1, then that means the producer is faster than the consumer.
                    System.out.println("Race condition detected! Consumer too slow");
                    System.exit(1);                    
                } else if(data == 0){ //If the data is 0, then the consumer is faster than the producer.
                    System.out.println("Race condition detected! Producer too slow");
                    System.exit(1);
                } else buffer[(next_out + i) % buffer.length] = 0; //If both producer and consumer caused no race condition to occur.
            }
            //Since we have emptied the buffer from buffer[next_in] until buffer[next_in + (k2 - 1)] 
            //(this is assuming we have not reached the end of the buffer while filling; else buffer index would have started back at 0 until i reached k1),
            //then next_out is now set to the next buffer slot that needs to be emptied. 
            next_out = (next_out + k2) % buffer.length;
            System.out.println("Round " + ++count + " no race condition detected yet"); //Loops until a race condition has been found.
        }
    }
}
/*OUTPUT
 Round 1 no race condition detected yet
Round 2 no race condition detected yet
Round 3 no race condition detected yet
Round 4 no race condition detected yet
Round 5 no race condition detected yet
Round 6 no race condition detected yet
Round 7 no race condition detected yet
Round 8 no race condition detected yet
Round 9 no race condition detected yet
Round 10 no race condition detected yet
Round 11 no race condition detected yet
Round 12 no race condition detected yet
Round 13 no race condition detected yet
Round 14 no race condition detected yet
Race condition detected! Consumer too slow

Round 1 no race condition detected yet
Round 2 no race condition detected yet
Round 3 no race condition detected yet
Race condition detected! Producer too slow

Round 1 no race condition detected yet
Round 2 no race condition detected yet
Round 3 no race condition detected yet
Round 4 no race condition detected yet
Round 5 no race condition detected yet
Round 6 no race condition detected yet
Round 7 no race condition detected yet
Round 8 no race condition detected yet
Race condition detected! Consumer too slow
 
 */
