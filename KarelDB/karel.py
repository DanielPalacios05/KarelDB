#TODO: Cargar un esquema de base de datos
#TODO: Implementar lock de entidades, cada entidad va a tener un lock
#TODO: Implementar update;
import json
import traceback
from kareldbinternals import KarelDbRepository
class KarelDB:

    def __init__(self,kareldb_repository: KarelDbRepository) -> None:

        self.repository = kareldb_repository 
        #Crear un nuevo proceso que llame un metodo start_server()
        pass
    """
    def load_db(self, schema_path : str):
        #TODO: Lock database, create transaction log for db
        with open(schema_path, "r") as archivo_json:
            newDb : dict= json.load(archivo_json) 
            if self.databases.get(newDb.get("db_name")) is None :
                self.databases[newDb.get("db_name")] = newDb

                for table in self.databases.get(newDb.get("db_name")).get("tables"):
                    table["rows"] = []
            else:
                raise Exception(f"{newDb.get("db_name")} already exists")
    """

    def search(self,dbName: str, tableName: str, columnName: str, value: str):
        
        database = self.databases.get(dbName)

        target_table = None
        for table in database["tables"]:
            if table["name"] == tableName:
                target_table = table
                break
        if target_table is None:
            print(f"Error: Table '{tableName}' not found in database '{dbName}'.")
            return
        # Find the column index in the table
        column_index = None
        for i, column in enumerate(target_table["columns"]):
            if column["name"] == columnName:
                column_index = i
                break
        if column_index is None:
            print(f"Error: Column '{columnName}' not found in table '{tableName}'.")
            return
        # Search for rows matching the value in the specified column
        matching_rows = []
        for row in target_table["rows"]:
            if str(row[column_index]) == value:
                matching_rows.append(row)

        return matching_rows
 
    def create(self,db_schema:dict):
        try:

            created_db = self.repository.create_db(db_schema)
            return 0, f"Database {created_db.get_name()} sucessfully created"
        except Exception as e:
             return {"status_code":1,"body":str(e)}


    def insert(self, db_name, table_name, record):
        # Insert operation
        try:
            #bloquemoas el archivo
            # response = repository.insert(db,table,record)
            # si la respues es bien:

                #Escribimos la transaccion en la ultilma linea (db, table. record)
                #desbloqueamos el archivo
                # retornamos lo que haya en respuesta kakaka
            # sino:
                # retornamos retornamos lo que haya en respuesta 
            # Your insert logic here
            self.repository.insert(db_name,table_name,record)

            return {"status_code":0,"body":"Valor insertado correctamente"}
        except Exception as e:
            return {"status_code":1,"body":str(e)}
        
    

    def update(self, db_name, table_name, record, where):
        try:
            rows_affected = self.repository.update(db_name,table_name,record,where)
            # Your update logic here
            return 0, "Operación realizada correctamente"
        except Exception as e:
             return {"status_code":1,"body":str(e)}

    def select(self, db_name, table_name, where):
        # Select operation
        try:
            rows = self.repository.select(db_name,table_name,where)
            # Your select logic here
            return {"status_code":0,"body":rows}
        except Exception as e:
             return {"status_code":1,"body":str(e)}
    def load_db(self,db_schema):
        try:
            # Your select logic here
            return {"status_code": 0, "body":f"DB {db_schema["db_name"]} sucessfully created"}
        except Exception as e:
             return {"status_code":1,"body":str(e)}

    def call(self, message:str):

        try:
            parts = message.split(' ', 1)
            operation = parts[0].upper();
            body = json.loads(parts[1].strip())
            if operation == "CREATE":
                return self.create(body)
            elif operation == 'INSERT':
                return self.insert(body['db_name'], body['table_name'], body['record'])
            elif operation == 'UPDATE':
                return self.update(body['db_name'], body['table_name'], body['record'], body['where'])
            elif operation == 'SELECT':
                return self.select(body['db_name'], body['table_name'], body['where'])
            else:
                return {"status_code": 1, "body":f"Unknown operation {operation}"}
        except Exception as e:
            print(traceback.format_exc())
            return {"status_code": 1, "body":f"Internal server error"}
 
"""
    def insert(self,dbName:str,tableName:str,values:dict):
        #TODO: Escribir la operacion de escritura en el log, modify
        table = self.databases.get(dbName).get("tables")


        # Crear una nueva fila con los datos proporcionados
        nueva_fila = [tipo_robot, id_robot, encendido, calle, avenida, beepers, direccion]
    
        # Agregar la nueva fila a la tabla "Robot" en el memory database
        memory_database["Robot"]["filas"].append(nueva_fila)
""" 

        


