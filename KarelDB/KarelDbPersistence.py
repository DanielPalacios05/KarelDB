import pathlib
from threading import Lock
class KarelDbPersistence():
    
    
    def __init__(self) -> None:
        self.dbLogFiles = {}
        self.rootDirName = "aof"
        self.basepath = pathlib.Path(__file__).parent.resolve() / self.rootDirName
        self.log_suffix = ".aof"
    def initialize(self):
        # check if dir at basepath exists
        if ((self.basepath).exists()):
            
            for file in self.basepath.glob("*"+self.log_suffix):
                
                self.dbLogFiles[file.stem] = DbLogFile(file.resolve())
                
                
        else:
            self.basepath.mkdir(parents=True)
    
    def create_file(self,db_name:str):    

        new_file_path = self.basepath / (db_name + self.log_suffix)
        if new_file_path.exists():
            raise IOError("File already exist")
        else:
            new_file_path.touch()
            self.dbLogFiles[db_name] = DbLogFile(new_file_path)
            
    
    def append_operation(self,db_name:str,operation:str):
        
        aof_file : DbLogFile = self.dbLogFiles.get(db_name)
        
        if aof_file is not None:
            
            aof_file.append_line(operation)
        else:
            IOError(f"aof file {db_name+self.log_suffix} not found")
    
    def get_aof_files(self):
        return self.dbLogFiles
            
            
            
            
class DbLogFile():
    
    def __init__(self, filepath : pathlib.Path) -> None:
        self.lock = Lock()
        self.filepath = filepath
        
    def append_line(self,line:str):
        self.lock.acquire()
        if self.filepath.exists():
            with self.filepath.open(mode="a",) as aof_file:                
                aof_file.write(line + "\n")
            self.lock.release()
        else:
            self.lock.release()
            raise IOError("File doesn't exist")
        
        
    def get_operations(self):
        
        self.lock.acquire()
        with self.filepath.open(mode="r",) as aof_file:                
            for line in aof_file.readlines():
                yield line
        self.lock.release()
        
    def get_file_name(self):
        
        return self.filepath.name
        
        
        
        
        
    

        
        
    
    
