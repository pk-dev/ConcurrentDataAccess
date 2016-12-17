/**
 * Created by Priya on 10/8/16.
 */
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.FileReader;

public class MainTester {

    //create semaphores
    static Semaphore buffer1Empty=new Semaphore(1);
    static Semaphore buffer1Full=new Semaphore(0);
    static Semaphore buffer2Empty=new Semaphore(1);
    static Semaphore buffer2Full=new Semaphore(0);

    //bufferer 1 and 2
    static BufferedReader buffer1;
    static BufferedReader buffer2;


    public static void main(String args[])
    {
        //call A, B and C as separate threads
        //B should be able to read data from buffer 1 only after A releases the lock on it
        //A should be able to print data from buffer 2 only after B releases the lock on it.
        new Thread(new ProcessC()).start();
        new Thread(new ProcessB()).start();
        new Thread(new ProcessA()).start();


    }

    private static class ProcessA implements Runnable {
        private volatile boolean execute;
        ProcessA()
        {
            System.out.println("ProcessA called.");
        }
        public void run()
        {
            try {
                this.execute=true;
                    //down semaphore buffer1Empty (waits if 0)
                    buffer1Empty.acquire();

                    //Read data from file and save to Buffer1
                    System.out.println("ProcessA in CS");
                    FileReader reader = new FileReader("./TestFileLocation/testfile.txt");
                    buffer1 = new BufferedReader(reader);

                    //up semaphore buffer1Full
                    buffer1Full.release();
                }
                catch (Exception e) {
                    e.getCause();
                    System.out.println(e.getMessage());

                }
                finally {
                    this.execute=false;
            }

        }

    }

    private static class ProcessB implements Runnable{
        private volatile boolean execute;
        ProcessB(){
            System.out.println("ProcessB called.");
        }
        public void run()
        {
            try {
                this.execute=true;
                    //down semaphore buffer1Full
                    buffer1Full.acquire();
                    //down semaphore buffer2Empty
                    buffer2Empty.acquire();

                    //Copy data from buffer1 to buffer2
                    System.out.println("ProcessB in CS");
                    buffer2 = new BufferedReader(buffer1);

                    //up semaphore buffer1Empty
                    buffer1Empty.release();
                    //up semaphore buffer2Full
                    buffer2Full.release();

                }
                catch (Exception e) {
                    e.getCause();
                    System.out.println(e.getMessage());
                }
                finally {
                    this.execute=false;
            }

        }

    }

    private static class ProcessC implements Runnable{
        private volatile boolean execute;
        ProcessC(){
            System.out.println("ProcessC called.");
        }
        public void run()
        {

            try {
                this.execute=true;
                    //down semaphore buffer2Full
                    buffer2Full.acquire();

                    //Print data from buffer2
                    System.out.println("ProcessC in CS");
                    System.out.println("Data from buffer2:");
                    String s = null;
                    while ((s = buffer2.readLine()) != null) {
                        System.out.println(s);
                    }
                    //up semaphore buffer2Empty
                    buffer2Empty.release();
                }
                catch (Exception e) {
                    e.getCause();
                    System.out.println(e.getMessage());
                }
                finally {
                    this.execute=false;
            }

        }

    }

}

