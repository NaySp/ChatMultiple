
//S
import java.io.Serializable;

public class Audio implements Serializable {
    private byte[] data;

    public Audio(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
