## Tipo de usuario:
La aplicación está pensada para ser usada por personas activas de cualquier edad que busquen cierta ayuda de gestión y organización del dia a dia.


## Función del calendario:

El objeto principal del calendario son los eventos, estos pueden ser de distintos tipos (eventos, tareas, recordatorios...) y tendrán asociadas unas etiquetas para permitir agrupar varios eventos con similar temática. Es posible que un evento tenga más de 1 etiquetas o ninguna.

### Funcionalidades:

- **[F0] Interacción por voz con el asistente** :  El asistente se comunicará por voz con el usuario para ejecutar cualquier funcionalidad.

- **[F1] Activación por voz del AV** :  El AV estará en modo espera en todo momento pero para acceder a cualquier funcionalidad previamente habrá que decir un código por voz para que este interprete nuestra petición.

- **[F2] Notificación de los eventos** :  El asistente notificará o no (a elección del usuario) los eventos cercanos siguiendo unas opciones predeterminadas (modo de aviso, fecha y hora), pero estas se podrán cambiar. El modo de aviso de un evento puede ser, por medio de una notificación o por voz.

- **[F3] Sincronización con Google Calendar** :  La aplicación hará uso de la API de Google Calendar para gestionar los eventos.

- **[F4] Gestionar etiquetas** : Se podrán crear, borrar y modificar etiquetas. 

- **[F5] Gestionar eventos** : Se podrán crear, borrar y modificar eventos, tanto el crear como el modificar permitirán asociar etiquetas al evento en cuestión, además habrá 2 maneras de crear un evento:
  - Una manera sencilla donde solo se pida la fecha, el nombre y el tipo de evento (eventos, tareas, recordatorios...), dejando todos los demás campos con unos valores predeterminados.
  - Una manera avanzada donde se permita personalizar todos los campos anteriores, además de configurar las notificaciónes y las etiquetas del evento.
  
- **[F6] Listar eventos por fecha** : Se podrá preguntar que eventos hay un dia en concreto o un tramo de tiempo (varios dias consecutivos o un tramo horario de un día en concreto). También esta búsqueda se podrá filtrar por etiquetas o por el tipo de evento.

- **[F7] Consultar fecha de evento por nombre** : Se podrá pedir al AV que recuerde la fecha de un evento por el nombre del evento.

- **[F8] Buscar hueco en calendario** :  El AV será capaz de buscar un hueco en el calendario para añadir un nuevo evento o actividad.

- **[F9] Comando de ayuda (opcional)** :  El AV dispondrá de una sección de preguntas frecuentes, orientada a enseñar sobre el uso básico de la aplicación. Se le podrá decir palabras clave (etiquetas, crear, recordatorios...) para que busque preguntas asociadas a dichas palabras. Posteriormente informará al usuario de las preguntas encontradas por si alguna se corresponde con la duda que tenía.
