# Descripción Chat Multiple:

Aplicativo que simula una sala de Chat donde se pueden crear grupos de chat, mandar mensajes privados y mandar audios, al igual que enviar mensajes a todos los clientes conectados. Consultar la guía proporcionada en clase para mas información. 

##### Compilar e iniciar el servidor:

Para iniciar el Server, ejecutar por comandos, el comando Server.java

##### Compilar e iniciar el cliente:

Para iniciar el Client, ejecutar por comandos, el comando Client.java

###### Nota Importante: 

Cada cliente debe de usar un nombre de usuario **diferente**. Cuando envia un mensaje a la sala el mensaje es enviado a todos incluido el usuario que envió el mensaje. Proponer una estrategia para evitar que el mismo usuario reciba el mensaje.

#### Enviar un mensaje privado: 

Si se desea enviar un mensaje privado se debe manejar la sintaxis de manera que:
usuarioDestino: + mensaje

Es decir, desde el chat del destinatario debe ingresar el nombre del usuario al que desea enviar el mensaje mas el mensaje.

**Ejemplo (Esta seria la vista desde comandos del usuario de samuel):**

Samuel (Usuario que desea enviar el mensaje)

Isabella: Hola 

**Vista desde el usuario de Isabella** 
(chat privado:) Samuel: Hola

#### Crear un grupo de chat

Si se deseas crear un grupo se utiliza el comando **/createGroup** +  **NOMBRE_GRUPO**

**Ejemplo:**

/createGroup Grupo1

Después de aplicar el comando se vera lo siguiente:

Grupo 'Grupo1' creado correctamente.

#### Entrar a un grupo

###### Nota importante:

Para entrar a un grupo debe estar creado antes.

Si se desea ingresar a un grupo se utiliza el comando **/join** + **NOMBRE_GRUPO**

**Ejemplo:**

/join Grupo1

Después de aplicar el comando se verá lo siguiente:

Te has unido al grupo 'Grupo1'.

#### Enviar mensaje a un grupo

Si desea enviar un mensaje a un grupo se utiliza el comando **/group** + **NOMBRE_GRUPO**  + **MENSAJE**

**Ejemplo:**

/group Grupo1 mensaje

Después de aplicar el comando se verá lo siguiente:

Mensaje enviado al grupo 'Grupo1'.

#### Salirse de un grupo

Si desea salirse de un grupo se utiliza el comando **/leaveGroup** + **NOMBRE_GRUPO**

**Ejemplo:**

/leaveGroup Grupo1 

Después de aplicar el comando se verá lo siguiente:

Mensaje enviado al grupo 'Grupo1'.

#### Enviar un audio a una persona

Para enviar un audio se utiliza el comando **/recordAudio** + **to** + **NOMBRE_USUARIO**

**Ejemplo:**

/recordAudio to Isabella

Después de aplicar el comando se empezara a grabar el audio y se enviara al otro usuario.

#### Enviar un audio a un grupo 

Para enviar un audio se utiliza el comando **/recordAudio** + **group** + **NOMBRE_GRUPO**

**Ejemplo: **

/recordAudio group Grupo1

Después de aplicar el comando se empezara a grabar el audio y se enviara al grupo.


# Integrantes:

- Isabella Ocampo Soto
- Valentina González Tapiero
- Samuel Álvarez Alban
- Nayeli Suarez Portillo
