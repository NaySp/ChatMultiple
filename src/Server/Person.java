import java.io.PrintWriter;
import java.net.Socket;

public class Person {
    private String name;
    private PrintWriter out;
    private Socket socket;

    public Person(String name, PrintWriter out, Socket socket){
        this.name = name;
        this.out  = out;
        this.socket = socket;
    }
   
    public String getName() {
        return name;
    }
    
    public PrintWriter getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }  
    
}
