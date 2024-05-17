import csv
import os
import subprocess

# Inicializar la variable global
linea = 0

# Función para ejecutar el código Java y capturar su salida
def ejecutar_codigo_java():
    global linea  # Utilizar la variable global

    # Obtener la ruta completa al archivo Minero.class
    ruta_archivo_java = ""
    # Comando para ejecutar el código Java con CLASSPATH explícito
    comando_java = ["java", "-cp", "KarelJRobot.jar", "Minero.java"]
    proceso = subprocess.Popen(comando_java, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    
    # Abrir el archivo CSV para escritura (se crea si no existe)
    with open("log.csv", "w", newline="", encoding="utf-8") as archivo_csv:
        escritor = csv.writer(archivo_csv)
        
        # Leer y escribir cada línea de la salida del proceso
        for linea_actual in proceso.stdout:
            linea_decodificada = linea_actual.decode().strip()
            escritor.writerow([linea_decodificada])
            archivo_csv.flush()  # Vaciar el búfer para asegurar que los datos se escriban en el archivo

            # Incrementar el contador de líneas
            linea += 1

            # También podemos imprimir cada línea en la consola de Python
            print(linea_decodificada)

    proceso.wait()  # Esperar a que el proceso termine

# Ejecutar el código Java y escribir la salida en el archivo CSV
ejecutar_codigo_java()

# Imprimir el número total de líneas
print("Número total de líneas:", linea)

print("Se ha creado el archivo log.csv con los textos del código Java.")
