import json
import threading
import karel
import socket
import copy
import signal
import sys
import pickle
from kareldbinternals import KarelDbRepository
from KarelDbPersistence import KarelDbPersistence
import time
from datetime import timedelta
from atomiccounter import AtomicCounter
class KarelDBServer:

    def __init__(self, host,port,) -> None:
            self.initTime = time.perf_counter()
            self.counter = AtomicCounter()
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
            self.host = host
            self.port = port
            self.sock.bind((host,port))
            self.isOn = True
            self.connections = []
            signal.signal(signal.SIGINT, self.__signal_handler__)
            self.karelDBinstance = karel.KarelDB(kareldb_repository=KarelDbRepository(),kareldb_persistence=KarelDbPersistence())
            self.message_size = 1024*1024 # Every message is maximum 1mb
            self.stoptime = None



    def start_server(self):
          self.karelDBinstance.initialize()
          self.sock.listen()
          print(f"KarelDB ready for accepting connections at {self.host}:{self.port}")

          while self.isOn:
                    conn, addr = self.sock.accept()
                    #self.__serve_request__(conn,addr)
                    connection_idx = len(self.connections)
                    th = threading.Thread(target=self.__serve_connection__,name=f"CON-{connection_idx}-{addr[0]}:{addr[1]}",args=(conn,addr,connection_idx))
                    th.start()
    def __serve_connection__(self,conn : socket.socket ,addr,connection_idx):

        connection_isOn = True
        self.__add_connection__(conn)
        print(f"{threading.current_thread()._name }: Initiated connection with client")
        while connection_isOn and self.isOn:
            request_msg = conn.recv(self.message_size).decode()
            if request_msg=='':
                  print(f"{threading.current_thread()._name}: Connection died")
                  self.__close_connection__(conn)
                  connection_isOn = False
            else:
                for req_msg in request_msg.split("\n")[:-1]:             
                  th = threading.Thread(target=self.__serve_request_,name=f"REQ-{connection_idx}-{addr[0]}:{addr[1]}",args=(conn,addr,req_msg))
                  th.start()
    
    def __serve_request_(self,conn: socket.socket,addr,request_msg):
            print(f"{threading.current_thread()._name }: {request_msg}")

            if self.isOn:
                self.counter.increment()
                response = self.karelDBinstance.call(request_msg)
                msg_response : str = f"{json.dumps(response)}\n" 
                conn.sendall(msg_response.encode())

    def __close_connection__(self,conn:socket.socket):
          conn.close()
          self.connections.remove(conn)
    def __add_connection__(self,conn):
          self.connections.append(conn)
    def __signal_handler__(self,sig, frame):
        print('Gracefully exiting...')
        self.isOn = False
        duration = timedelta(seconds=time.perf_counter()-self.initTime)
        seconds_elapsed = duration.total_seconds()
        for conn in self.connections:
              self.__close_connection__(conn)
        self.sock.close()
        print(f"time elapsed: {duration.seconds} seconds \n total requests: {self.counter.value} \n request/sec:{self.counter.value/seconds_elapsed}")
        print("Bye!")
        sys.exit(0)


          