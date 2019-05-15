import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try(ServerSocket s = new ServerSocket(8189)){
            int i=1;

            while(true){
                Socket incoming = s.accept();
                System.out.println("Spawning "+ i );
                Runnable r = new ThreadedEchoHandler(incoming);
                Thread t = new Thread(r);
                t.start();
                i++;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
/**
 * This class handles the client input for one server socket connection.
 */
class ThreadedEchoHandler implements Runnable{
    private Socket incoming;

    /**
     * Constructs a handler.
     * @param incomingSocket the incoming socket
     */
    public ThreadedEchoHandler(Socket incomingSocket){
        incoming = incomingSocket;
    }

    public void run(){
        try(InputStream inStream = incoming.getInputStream(); OutputStream outStream = incoming.getOutputStream()){
            Scanner in = new Scanner(inStream, "UTF-8");
            PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream,"GBK"),true/*autoFlush*/);
            out.println("Hello!");

            try{
                MineCleaningOnline mco = new MineCleaningOnline();
                mco.play(out,in);
            }
            catch(Exception e){
                for(int i=0;i<5;i++)out.println();
                out.println("=============================================");
                out.println("Shit,服务器线程崩了。Message:"+e.getMessage()+" Cause:"+e.getCause());
                e.printStackTrace();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}