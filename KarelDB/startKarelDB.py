from kareldbserver import KarelDBServer
import sys



def main():
    karelDB = KarelDBServer(sys.argv[1],int(sys.argv[2]))
    karelDB.start_server()
    

if __name__ == "__main__":
    main()