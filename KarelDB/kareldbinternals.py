from threading import Lock
from dateutil import parser
import datetime
class KarelDbRepository:

    def __init__(self) -> None:
        self.repository = {}
        self.datatypes = ["integer", "string", "datetime", "boolean","array"]

    
    # Creates a db based on the defined schema
    # Checks if there's a db with the same name
    def create_db(self,db_schema:dict):

        db_name = db_schema.get("db_name")

        if self.repository.get(db_name) is None:
            new_database = KarelDatabase(db_name)
            for table in db_schema.get("tables"):
                new_table = KarelTable(table.get("name"))
                for schema_column in table.get("columns"):
                    if schema_column.get("type") in self.datatypes:
                        new_column = KarelColumn(schema_column.get("name"),schema_column.get("type"),schema_column.get("foreign_key"))
                    else:
                        raise DBException(f"Unsupported datatype {schema_column.get("type")}")
                    new_table.add_column(new_column)  
                new_database.add_table(new_table)
            self.repository[db_name] = new_database

            return new_database
        else:
            raise DBException("Database already exists")
    
    def insert(self,db_name:str,table_name:str,values:dict):

        target_db : KarelDatabase = self.repository.get(db_name)

        if target_db is None:
            raise DBException(f"Database {db_name} doesn't exist")
        
        target_table : KarelTable = target_db.get_table(table_name)
        if target_table is None:
            raise DBException(f"Table {table_name} not found in database '{db_name}'.")
        # Check if the number of provided values matches the number of columns in the table
        if len(values) != len(target_table.get_columns_names()):
            raise DBException(f"Number of values does not match number of columns in table. received {len(values)} expected {len(target_table.get_columns_names())}")
            
        # Create a new row based on the provided values
        new_row = {}
        for column_name in target_table.get_columns_names():
            column = target_table.get_column(column_name)
            if column_name not in values:
                raise DBException(f"Missing value for column '{column_name}'")
            
            column_datatype = column.get_datatype()
            # Check data type and cast if necessary
            if column_datatype == "integer":
                try:
                    new_row[column_name] = int(values.get(column_name))
                except ValueError:
                    raise DBException(f"Value for column '{column_name}' must be an integer.")
            elif column_datatype == "boolean":
                try:
                    new_row[column_name] = bool(values.get(column_name))
                except Exception:
                    raise DBException(f"Value for column '{column_name}' must be boolean or can't be converted to boolean")
            elif column_datatype == "string":
                new_row[column_name] = str(values[column_name])
            elif column_datatype == "datetime":
                new_row[column_name] = parser.parse(values.get(column_name))
            elif column_datatype == 'array':
                new_row[column_name] = list(values.get(column_name))
            else:
                raise DBException(f"Unsupported data type '{column_datatype}' for column '{column_name}'.")
    
        # Add the new row to the table
        target_table.add_row(KarelRow(new_row))
        # Save the updated database structure back to file
        return True
    def update(self,db_name:str,table_name:str,record:dict,where:dict):
        
        affected_rows = 0
        
        target_db : KarelDatabase = self.repository.get(db_name)

        if target_db is None:
            raise DBException(f"Database {db_name} doesn't exist")
        
        target_table : KarelTable = target_db.get_table(table_name)
        if target_table is None:
            raise DBException(f"Table {table_name} not found in database '{db_name}'.")
        
        selected_rows : list[KarelRow]= target_table.select_rows(where)
        
        for row in selected_rows:
            
           
                for field,value in record.items():
                    
                    if field not in target_table.get_columns_names():
                        raise DBException(f"field is not in {target_table} table")

                    column = target_table.get_column(field)
                    if column.validate_column_value(value):
                        try:
                            row.update_row(record)
                        except DBException as updateError:
                            raise DBException(f"{str(updateError)},at {target_table}")

                        affected_rows+=1
        for row in selected_rows:
            row.release_row()




        
    
    def select(self,db_name:str,table_name:str,where:dict) :

        target_db : KarelDatabase = self.repository.get(db_name)

        if target_db is None:
            raise DBException(f"Database {db_name} doesn't exist")
        
        target_table : KarelTable = target_db.get_table(table_name)
        if target_table is None:
            raise DBException(f"Table {table_name} not found in database '{db_name}'.")
        
        selected_row_fields = []

        selected_rows = target_table.select_rows(where)

        for row in selected_rows:
            selected_row_fields.append(row.fields)

        for row in selected_rows:
            row.release_row()

        return selected_row_fields

class DBException(Exception):
    def __init__(self, message=None):
        self.message = message
        super().__init__(message)
    
class KarelRow():
    
    def __init__(self, fields:dict) -> None:
        self.lock = Lock()
        self.fields = fields
    def lock_row(self):
        self.lock.acquire()
    def release_row(self):
        self.lock.release()

    def get_fields(self):
        return self.fields
    def update_field(self,field,value):
        
         if field not in self.fields:
                raise DBException(f"Field '{field}' not found")
         self.fields[field] = value

    def update_row(self,record:dict):

        for field,value in record.items():

            if field not in self.fields:
                raise DBException(f"Field '{field}' not found")
            else:
                self.fields[field] = value
    
    def matches(self,cond:dict):

        for field,value in cond.items():

            if field not in self.fields:
                return False
            
            if value != self.fields.get(field):
                return False
        
        return True
    



class KarelColumn():

    def __init__(self,column_name,datatype,foreing_key:None | dict=None) -> None:
        self.name = column_name
        self.datatype = datatype
        self.foreign_key = foreing_key

    def get_name(self):
        return self.name
    def get_datatype(self):
        return self.datatype
    def get_foreign_key(self)-> dict | None:
        return self.foreign_key
    def validate_column_value(self,value):
    
        if self.datatype == "integer":
            python_type = int
        elif self.datatype == "boolean":
            python_type = bool
        elif self.datatype == "string":
            python_type = str
        elif self.datatype == "datetime":
            python_type = datetime.datetime
        elif self.datatype == 'array':
            python_type = list
        
        if type(value) is not python_type:
            return False
        
        # TODO: Setup referential integrity
        
        if self.foreign_key != None:
            
            table_name = self.foreign_key["table_name"]
            
        
        return True
            
        
class KarelTable():

    def __init__(self, table_name:str) -> None:
        self.name = table_name
        self.columns = {}
        self.rows : list[KarelRow] = [] 
    
    def add_column(self,column: KarelColumn):
        self.columns[column.get_name()] = column
    
    def get_name(self):
        return self.name
    
    def get_columns_names(self):
        return self.columns.keys()
    def get_column(self,column_name) -> KarelColumn | None:
        return self.columns.get(column_name)
    def add_row(self,new_row : KarelRow):
        self.rows.append(new_row)
    def select_rows(self,where:dict | str):

        selected_rows : list[KarelRow] = []
        
        if type(where) is str and where == "ALL":
            for row in self.rows:
                row.lock_row()
                selected_rows.append(row)
        elif type(where) is dict:
            for row in self.rows:
                row.lock_row()

                if row.matches(where):
                    selected_rows.append(row)
                else:
                    row.release_row()
            
        return selected_rows





class KarelDatabase():

    def __init__(self,db_name:str) -> None:
        self.name = db_name
        self.tables = {}
    def add_table(self,new_table: KarelTable):
        self.tables[new_table.get_name()] = new_table 
    def get_name(self):
        return self.name
    def get_table(self,table_name) -> KarelTable | None:
        return self.tables.get(table_name)
    
        

