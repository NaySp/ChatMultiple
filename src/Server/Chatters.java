import java.util.Set;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Chatters {
    // Conjunto de personas conectadas al chat
    private Set<Person> clientes = new HashSet<>();
    private Map<String, Set<Person>> grupos = new HashMap<>();

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

    // Devuelve el flujo de salida de un usuario en línea
    public PrintWriter getUserStream(String name) {
        for (Person person : clientes) {
            if (person.getName().equalsIgnoreCase(name)) {
                return person.getOut();
            }
        }
        return null; // Retornar null si el usuario no está en línea o no existe
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
            guardarMensaje(p.getName(), message); // Guardar el mensaje en el historial del cliente
        }
    }

    // Metodo para enviar a un usuario en especifico
    public void sendMessageToUser(String Clientname, String addressee, String message) {
        if (existeUsr(addressee)) {
            synchronized (clientes) {
                for (Person user : clientes) {
                    if (user.getName().equalsIgnoreCase(addressee)) {
                        try {
                            user.getOut().println("(Chat privado:)" + Clientname + ": " + message);
                            guardarMensaje(addressee, "(Chat privado:)" + Clientname + ": " + message); // Guardar el
                                                                                                        // mensaje en el
                                                                                                        // historial del
                                                                                                        // cliente
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            broadcastMessage(message);
            guardarMensaje(Clientname, message); // Guardar el mensaje en el historial del cliente que lo envió
        }
    }

    public void guardarMensaje(String cliente, String mensaje) {
        // Buscar al cliente en el conjunto de clientes
        for (Person p : clientes) {
            if (p.getName().equals(cliente)) {
                // Escribir el mensaje en el archivo del cliente
                try (FileWriter fileWriter = new FileWriter(cliente + ".txt", true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
                    printWriter.println(mensaje);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void createGroup(String groupName) {
        grupos.put(groupName, new HashSet<>());
    }

    // Método para agregar un usuario a un grupo
    public void addUserToGroup(String groupName, Person person) {
        if (grupos.containsKey(groupName)) {
            grupos.get(groupName).add(person);
            person.getOut().println("Te has unido al grupo '" + groupName + "'.");
        } else {
            person.getOut().println("El grupo '" + groupName + "' no existe.");
        }
    }

    // Método para enviar un mensaje a un grupo
    public void sendMessageToGroup(String groupName, String message) {
        if (grupos.containsKey(groupName)) {
            for (Person person : grupos.get(groupName)) {
                person.getOut().println(message);
            }
        }
    }

    public void removeUserFromGroup(String groupName, String userName) {
        if (grupos.containsKey(groupName)) {
            Set<Person> groupMembers = grupos.get(groupName);
            for (Person person : groupMembers) {
                if (person.getName().equals(userName)) {
                    groupMembers.remove(person);
                    person.getOut().println("Te has salido del grupo '" + groupName + "'.");
                    return;
                }
            }
        }
    }
}
