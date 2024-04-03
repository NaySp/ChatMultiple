import java.util.Set;
import java.io.PrintWriter;
import java.util.HashSet;

public class Chatters {
    // Conjunto de personas conectadas al chat
    private Set<Person> clientes = new HashSet<>();

    public Chatters() {
    }

    // Verifica si un usuario ya existe en el chat
    public boolean existeUsr(String name) {
        boolean response = false;
        for (Person p : clientes) {
            if (name.equals(p.getName())) {
                response = true;
                break;
            }
        }
        return response;
    }

    // Agrega un usuario al chat si el nombre no está vacío y no está en uso
    public void addUsr(String name, PrintWriter out) {
        if (!name.isBlank() && !existeUsr(name)) {
            Person p = new Person(name, out);
            clientes.add(p);
        }
    }

    // Elimina un usuario del chat
    public void removeUsr(String name) {
        for (Person p : clientes) {
            if (name.equals(p.getName())) {
                clientes.remove(p);
                break;
            }
        }
    }

    // Envía un mensaje a todos los usuarios del chat
    public void broadcastMessage(String message) {
        for (Person p : clientes) {
            p.getOut().println(message);
        }
    }


    // Metodo para enviar a un usuario en especifico
    public void sendMessageToUser(String Clientname, String  addressee, String message) {
        if(existeUsr(addressee)){
            synchronized (clientes) {
                for (Person user : clientes ){
                    if(user.getName().equalsIgnoreCase(addressee)){
                        try {
                            user.getOut().println("(Chat privado:)" + Clientname + ": " + message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else{
            broadcastMessage(message);
        }
    }

}
