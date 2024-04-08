# Descripción:

Aplicativo que simula una sala de Chat
Consultar la guía proporcionada en clase

Modulo Server
Compilar y ejecutar Server.java

Modulo Client
Compilar y ejecutar Client.java

Cada cliente debe usar un nombre de usuario
diferente. Cuando envía un mensaje a la sala
el mensaje es enviado a todos incluido el usuario
que envió el mensaje. Proponer una estrategia 
para evitar que el mismo usuario reciba el mensaje.

Si se desea enviar un mensaje privado se debe manejar la sintaxis de manera que:
usuarioDestino:mensaje

Es decir, desde el chat del destinatario debe ingresar el nombre de hacia quien va el mensaje, ejemplo:

Usuario1
Usuario2: hola

Entonces Usuario2 verá lo siguiente:
(chat privado:) Usuario1: hola

si se deseas crear un grupo se utiliza el comando /createGroup NOMBRE_GRUPO
ejemplo: /createGroup Grupo1
Entonces se verá lo siguiente:
Grupo 'Grupo1' creado correctamente.

si se deseas ingresar a un grupo se utiliza el comando /join NOMBRE_GRUPO
ejemplo: /join Grupo1
Entonces se verá lo siguiente:
Te has unido al grupo 'Grupo1'.

si se deseas enviar un mensaje a un grupo se utiliza el comando /group NOMBRE_GRUPO MENSAJE
ejemplo: /group Grupo1 mensaje
Entonces se verá lo siguiente:
Mensaje enviado al grupo 'Grupo1'.

si se deseas salirte de un grupo se utiliza el comando /leaveGroup NOMBRE_GRUPO
ejemplo: /group Grupo1 mensaje
Entonces se verá lo siguiente:
Mensaje enviado al grupo 'Grupo1'.




# Integrantes:

- Isabella Ocampo
- Valentina Gonzales
- Samuel Alvarez
- Nayeli Suarez
