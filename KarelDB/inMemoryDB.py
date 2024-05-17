import json
import os
# Cargar la estructura de la base de datos desde un archivo
def cargar_base_de_datos_desde_archivo(ruta_archivo):
    with open(ruta_archivo, "r") as archivo_json:
        return json.load(archivo_json)

# Crear un "memory database" a partir de la estructura cargada
def crear_memory_database(estructura_base_de_datos):
    memory_database = {}
    for nombre_tabla, datos_tabla in estructura_base_de_datos["tablas"].items():
        columnas = datos_tabla["columnas"]
        filas = datos_tabla["filas"]
        memory_database[nombre_tabla] = {"columnas": columnas, "filas": filas}
    return memory_database

def agregar_fila_robot(memory_database, tipo_robot, id_robot, encendido, calle, avenida, beepers, direccion):
    # Obtener las columnas de la tabla "Robot"
    columnas = memory_database["Robot"]["columnas"]
    
    # Crear una nueva fila con los datos proporcionados
    nueva_fila = [tipo_robot, id_robot, encendido, calle, avenida, beepers, direccion]
    
    # Agregar la nueva fila a la tabla "Robot" en el memory database
    memory_database["Robot"]["filas"].append(nueva_fila)
    
    # Mostrar un mensaje de éxito
    print("Se ha agregado una nueva fila a la tabla 'Robot'.")

# Función para buscar filas en una tabla de la in-memory database
def buscar_filas(memory_database, tabla, columna, valor):
    # Verificar si la tabla especificada existe en el memory database
    if tabla not in memory_database:
        print(f"Error: La tabla '{tabla}' no existe en el memory database.")
        return []

    # Obtener las columnas de la tabla
    columnas = memory_database[tabla]["columnas"]
    
    # Verificar si la columna especificada existe en la tabla
    if columna not in columnas:
        print(f"Error: La columna '{columna}' no existe en la tabla '{tabla}'.")
        return []

    # Lista para almacenar las filas que coinciden con el criterio
    filas_coincidentes = []

    # Obtener el índice de la columna
    indice_columna = columnas.index(columna)

    # Iterar sobre las filas de la tabla
    for fila in memory_database[tabla]["filas"]:
        # Verificar si la fila coincide con el criterio
        if fila[indice_columna] == valor:
            filas_coincidentes.append(fila)

    # Imprimir cada fila en una nueva línea
    for fila in filas_coincidentes:
        print(fila)

    return filas_coincidentes

# Función para agregar una nueva fila a la tabla "logEventos" en el memory database
def agregar_fila_log_eventos(memory_database, time_stamp, id_robot, avenida, calle, beepers):
    # Verificar si la tabla "logEventos" existe en el memory database
    if "logEventos" not in memory_database:
        print("Error: La tabla 'logEventos' no existe en el memory database.")
        return

    # Obtener las columnas de la tabla "logEventos"
    columnas = memory_database["logEventos"]["columnas"]

    # Crear una nueva fila con los datos proporcionados
    nueva_fila = [time_stamp, id_robot, avenida, calle, beepers]

    # Agregar la nueva fila a la tabla "logEventos" en el memory database
    memory_database["logEventos"]["filas"].append(nueva_fila)

    # Mostrar un mensaje de éxito
    print("Se ha agregado una nueva fila a la tabla 'logEventos'.")

# Función para agregar una nueva fila a la tabla "EstadoPrograma" en el memory database
def agregar_fila_estado_programa(memory_database, time_stamp, estado):
    # Verificar si la tabla "EstadoPrograma" existe en el memory database
    if "EstadoPrograma" not in memory_database:
        print("Error: La tabla 'EstadoPrograma' no existe en el memory database.")
        return

    # Obtener las columnas de la tabla "EstadoPrograma"
    columnas = memory_database["EstadoPrograma"]["columnas"]

    # Crear una nueva fila con los datos proporcionados
    nueva_fila = [time_stamp, estado]

    # Agregar la nueva fila a la tabla "EstadoPrograma" en el memory database
    memory_database["EstadoPrograma"]["filas"].append(nueva_fila)

    # Mostrar un mensaje de éxito
    print("Se ha agregado una nueva fila a la tabla 'EstadoPrograma'.")








#identificar ruta actual
directorio_actual = os.path.dirname(os.path.abspath(__file__))
# Construir la ruta al archivo JSON
ruta_archivo_estructura = os.path.join(directorio_actual, "estructura.json")
#Carga la base de datos desde la plantilla
estructura_base_de_datos = cargar_base_de_datos_desde_archivo(ruta_archivo_estructura)

print("schema",estructura_base_de_datos)
#crea la in memory DB
memory_database = crear_memory_database(estructura_base_de_datos)
# Ahora `memory_database` contiene la estructura de la base de datos en la memoria

# Ejemplos de uso
agregar_fila_robot(memory_database, "Minero", 1, "on", 5, 6, 50, "north")
agregar_fila_robot(memory_database, "Tren", 1, "on", 5, 6, 50, "north")
agregar_fila_log_eventos(memory_database, "2024-04-30 12:00:00", 1, 5, 6, 10)
agregar_fila_estado_programa(memory_database, "2024-04-30 12:00:00", "Activo")

resultado_busqueda = buscar_filas(memory_database, "Robot", "tipoRobot", "Minero")
