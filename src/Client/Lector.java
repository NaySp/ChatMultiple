import java.io.*;

public class Lector implements Runnable {
    private BufferedReader in;

    // Constructor que recibe un BufferedReader para leer mensajes del servidor
    public Lector(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            // Leer mensajes del servidor línea por línea y mostrar en pantalla
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            // Manejar cualquier excepción de E/S imprimiendo la traza de la pila
            e.printStackTrace();
        } finally {
            // Cerrar el flujo de entrada una vez que se termina de leer
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
