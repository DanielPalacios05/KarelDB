import socket
import time
import sys
sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

sock.connect(("127.0.0.1",int(sys.argv[2])))
print("connected")
while True:
    msg = f"message-{sys.argv[1]}"
    print(msg)
    sock.sendall(msg.encode())

    time.sleep(5)