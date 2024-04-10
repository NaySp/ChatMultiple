import java.util.Set;
import java.io.PrintWriter;
import java.net.Socket;

//
public class Person {
    private String name;
    PrintWriter out;
    private AudioRecorder audioRecorder;

    public Person(String name, PrintWriter out){
        this.name = name;
        this.out  = out;
    }
   
    public String getName() {
        return name;
    }
    
    public PrintWriter getOut() {
        return out;
    }

    
    public AudioRecorder  getAudioRecorder(){
        audioRecorder =new AudioRecorder();
        return audioRecorder;
    }

}