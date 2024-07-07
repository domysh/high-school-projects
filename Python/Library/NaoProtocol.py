"""

    --> Basics NaoProtocol <--
        By DomySh | * _ * |!
       GNU Public License 3.0
 For Team JustNao (Nao Challenge 2020)
   Last Change: 06/02/2020 09:47 AM

"""
#-------------------------------------------------------------------------------------
#                                     Imports
#-------------------------------------------------------------------------------------

import socket, sys, time
from select import select
from _socket import SOL_SOCKET, SO_REUSEADDR
from threading import Thread
from hashlib import sha256
import traceback

#-------------------------------------------------------------------------------------
#                               Global Variables
#-------------------------------------------------------------------------------------

RECV = 1024
DEF_PORT = 8010

USER = "nao"
HOME = "/home/"+USER+"/"

#Connection Commands
CHK_COMMAND = "!CHK"
PROTOCOL_NAME = "@protocol.NAO"

SERVER_STOP = "!EXIT"

CLIENT_STOP = "@EXIT"

CLIENT_STOPPED = "@SUBDEL"

SUB_NAO = "!NAOSUB"
SUB_COMM = "!SUB"
SUB_ERROR = "@SUBERROR"
SUB_SUCCESS = "@SUBSUCCESS"

WHOAMI = "!WHOAMI"
NAO_SUBBED = "@SUBBEDNAO"
CLIENT_SUBBED = "@SUBBEDCLIENT"

ALL_DONE = PROTOCOL_NAME+":Done"
ALL_BAD = PROTOCOL_NAME+":Bad"

#Other commands
FILESEND_COMMAND = "!SENDFILE"
FILESEND_STOP_COMMAND = "!SENDFILESTOP"

FILESEND_START_COMMAND = "@SENDFILE.START"
FILESEND_SUCCESS = "@SENDFILE.SUCCESS"
FILESEND_FAIL = "@SENDFILE.ERROR"
FILESEND_GO = "@SENDFILE.GO"

#-------------------------------------------------------------------------------------
#                               Nao Client Object
#-------------------------------------------------------------------------------------

class NaoClient:

#-------------------------------------------------------------------------------------
#                               Client Connection
#-------------------------------------------------------------------------------------

    def __init__(self,ip,port=DEF_PORT):
        if ip[0] == ';':
            ip = self.broadcastListen(ip)
        self.SK = socket.socket()
        self.SK.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
        self.SK.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEPORT,1)
        self.SK.connect((str(ip),int(port)))
        self.SK.send(CHK_COMMAND.encode())
        data = self.SK.recv(RECV).decode()
        if data == PROTOCOL_NAME:
            self.SK.send(SUB_COMM.encode())
            data = self.SK.recv(RECV).decode()
            if data == SUB_ERROR:
                self.SK.close()
                self.SK = None
            elif data != SUB_SUCCESS:
                self.SK = None
        elif data == ALL_DONE:
            print("@> Client Just Registrated")
        else:
            self.SK = None
        if self.SK is None:
            print("!> Nao Connection Failed")
        else:
            print("@> Nao Connection Done")
        self.SUBBED_AS_NAO = False

#-------------------------------------------------------------------------------------
#                           Broadcast Wait Signal
#-------------------------------------------------------------------------------------
    def broadcastListen(self,server_name):
        client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP) # UDP
        client.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        client.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
        # Enable broadcasting mode
        client.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        client.bind(("", 37020))
        while True:
            # Thanks @seym45 for a fix
            data, addr = client.recvfrom(1024)
            if PROTOCOL_NAME+server_name == data.decode():
                return addr[0]

#-------------------------------------------------------------------------------------
#                         Basic Communication Commands
#-------------------------------------------------------------------------------------

    def sendMessage(self,data):
        self.SK.send(data)
    
    def sendStrMessage(self,a_string):
        print(f"/> Sending... -> {a_string}")
        self.sendMessage(a_string.encode())

    def getMessage(self):
        return self.SK.recv(RECV)

    def getStrMessage(self):
        return self.getMessage().decode()

#-------------------------------------------------------------------------------------
#                             File Send Manegment
#-------------------------------------------------------------------------------------

    def sendFile(self,f):
        f = open(f,'rb')
        self.sendStrMessage(FILESEND_COMMAND)
        if self.getStrMessage() == FILESEND_START_COMMAND:
            f.seek(0)
            l = f.read(RECV-1)
            while (l):
                self.sendMessage((b'F')+l)
                l = f.read(RECV-1)
                if self.getStrMessage() != FILESEND_GO:
                    return False
            f.seek(0)
            hashing = sha256(f.read()).hexdigest()
            self.sendStrMessage(FILESEND_STOP_COMMAND+";"+hashing)
            f.seek(0)
            if self.getStrMessage() == FILESEND_SUCCESS:
                return True
            else:
                return False
        else:
            return False
#-------------------------------------------------------------------------------------
#                                Other Commands
#-------------------------------------------------------------------------------------
    def subNao(self):
        req = SUB_NAO+';'+socket.gethostname()
        self.SK.send(req.encode())
        if self.SK.recv(RECV).decode() == SUB_SUCCESS:
            self.SUBBED_AS_NAO = True
            print("!> Registrated as NAO Robot")
            return True
        return False
    
    def naoSubbed(self):
        self.SK.send(WHOAMI.encode())
        if self.SK.recv(RECV).decode() == NAO_SUBBED:
            self.SUBBED_AS_NAO = True
        else:
            self.SUBBED_AS_NAO = False
        return self.SUBBED_AS_NAO

    def nao(self):
        return self.subNao() or self.naoSubbed()

#-------------------------------------------------------------------------------------
#                            Client Listen Loop
#-------------------------------------------------------------------------------------

    def loop(self):
            print(f"@> Nao Client Listener Started!")
            try:
                while(True): 
                    data = self.SK.recv(RECV)
                    if data:
                        data = data.decode()
                        if data == NAO_SUBBED:
                            self.naoSubbed()
                            print("!> Registrated as NAO Robot")
                        elif data == CLIENT_STOP:
                            print("!> Client Closed by a server request...")
                            break
                        else: 
                            self.manage(data)
#-------------------------------------------------------------------------------------
#           General Exception Manegment
#-------------------------------------------------------------------------------------
                self.close()
            except KeyboardInterrupt:
                print("\n!> CLient interrupted by user!")
                self.close()
            except Exception as e:
                print(f"\n!> Si è verificato un problema...\n{e}")
                traceback.print_exc()
                self.close()

#-------------------------------------------------------------------------------------
#                               Extra Commands
#------------------------------------------------------------------------------------- 

    def manage(self,comm):
        print(f"Command {comm} Not Managed")   

#-------------------------------------------------------------------------------------
#                               Client Close
#-------------------------------------------------------------------------------------

    def close(self):
        try: self.SK.send(CLIENT_STOPPED.encode())
        except:pass
        self.SK.close()

#-------------------------------------------------------------------------------------
#                               Nao Server Object
#-------------------------------------------------------------------------------------  

class NaoServer:
#-------------------------------------------------------------------------------------
#                               Server Binding
#-------------------------------------------------------------------------------------
    def __init__(self,public_name=None,ip=None,port=DEF_PORT):
        self.PORT = int(port)
        if ip is None: self.IP = get_ip()
        else: self.IP = str(ip)
        self.SK = socket.socket()
        self.SK.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.SK.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEPORT,1)
        self.BROADLOOP = False
        try:
            self.SK.bind((self.IP, self.PORT))
            self.SK.listen(10)
            self.LOOPVET = [self.SK]
            self.NAOSK = None
            self.NAONAME = None
            print(f"@> Nao Server Created on {self.IP}:{self.PORT}")
            if public_name is not None:
                if public_name[0] == ';':
                    self.publicServerOnBroadcast(public_name)
                else:
                    raise Exception('No Valid public name! Must start with ;')

        except OSError:
            print("Address already in use or not valid")

#-------------------------------------------------------------------------------------
#                           Broadcast Wait Signal
#-------------------------------------------------------------------------------------

    def publicServerOnBroadcast(self,name):
        self.SERVERNAME = name
        self.BROADLOOP = True
        dem = Thread(target=self.broadcastLoop)
        dem.daemon = True
        dem.start()

    def broadcastLoop(self):
        try:
            server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
            server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
            server.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
            # Enable broadcasting mode
            server.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
            # Set a timeout so the socket does not block
            # indefinitely when trying to receive data.
            server.settimeout(0.2)
            message = (PROTOCOL_NAME+self.SERVERNAME).encode()
            while self.BROADLOOP:
                server.sendto(message, ('<broadcast>', 37020))
                time.sleep(1)
        except:
            print("!> Broadcast Failed!")

#-------------------------------------------------------------------------------------
#                               General Function
#-------------------------------------------------------------------------------------

    def getIp(self):
        return self.IP
    
    def getPort(self):
        return self.PORT

    def sendToNao(self,data):
        if self.NAOSK is not None:
            self.NAOSK.send(data)
    
    def sendStrToNao(self,a_str):
        self.sendToNao(a_str.encode())

    def getMessageNao(self):
        if self.NAOSK is not None:
            return self.NAOSK.recv(RECV)
    
    def getStrMessageNao(self):
        r = self.getMessageNao()
        if r is not None:
            return r.decode()
            
    def getNaoRobotName(self):
        if self.NAOSK is not None:
            return self.NAONAME

#-------------------------------------------------------------------------------------
#                               Server Loop
#-------------------------------------------------------------------------------------

    def loop(self):
        print(f"@> Nao Server started!")
        sendfile = []
        try:
            while(True):
                lst ,_,_ =select(self.LOOPVET,[],[])
                for sk in lst:
#-------------------------------------------------------------------------------------
#                   Manage Connection Requests
#-------------------------------------------------------------------------------------  
                    if sk == self.SK:
                        self.connectReq()
                    else:
                        data = sk.recv(RECV)
                        if data:
#-------------------------------------------------------------------------------------
#                           File Transfer Manegment
#-------------------------------------------------------------------------------------                            
                            if sk in sendfile:
                                ind = sendfile.index(sk)
                                try: cond = data.decode().startswith(FILESEND_STOP_COMMAND)
                                except UnicodeDecodeError: cond = False
                                if cond:
                                    toread = sendfile[ind+1].name
                                    sendfile[ind+1].close()
                                    myHash = sha256(open(toread,"rb").read()).hexdigest()
                                    sendedHash = data.decode()[len(FILESEND_STOP_COMMAND)+1:]
                                    del (sendfile[ind+1])
                                    del (sendfile[ind])
                                    if(myHash == sendedHash):
                                        print(f"@> File sended Successfull:{sendedHash}")
                                        sk.send(FILESEND_SUCCESS.encode())
                                    else:
                                        print(f"@> File sending Failed:{sendedHash}")
                                        sk.send(FILESEND_FAIL.encode())
                                else:
                                    sendfile[ind+1].write(data[1:])
                                    sk.send(FILESEND_GO.encode())
                                continue
#-------------------------------------------------------------------------------------
#                           Commands
#-------------------------------------------------------------------------------------  
                            data = data.decode()
                            if data == CHK_COMMAND:
                                sk.send(PROTOCOL_NAME.encode())
                            elif data == SUB_COMM:
                                sk.send(SUB_SUCCESS.encode())
                            elif data == FILESEND_COMMAND:
                                print("@> Recived File Send Request")
                                sk.send(FILESEND_START_COMMAND.encode())
                                f = open(f"file_{sk.getpeername()[0]}.download","wb")
                                sendfile.append(sk)
                                sendfile.append(f)
                            elif data.startswith(SUB_NAO):
                                print(f"?> Nao Sub Request {sk.getpeername()[0]}")
                                if self.NAOSK is None:
                                    self.NAOSK = sk
                                    self.NAONAME = data[len(SUB_NAO)+1:]
                                    sk.send(SUB_SUCCESS.encode())
                                    print(f"?> Nao {self.NAONAME} Request Accepted")
                                else:
                                    sk.send(SUB_ERROR.encode())
                                    print("?> Nao Request Refused")
                            elif data == WHOAMI:
                                print("@> Identify Command Recived")
                                if sk == self.NAOSK:
                                    sk.send(NAO_SUBBED.encode())
                                else:
                                    sk.send(CLIENT_SUBBED.encode())
                            elif data == SERVER_STOP:
                                print("!> The server is closing by STOP command...")
                                self.close()
                                break
                            elif data == CLIENT_STOPPED:
                                print(f"@> {sk.getpeername()[0]} Disconnected!")
                                if sk == self.NAOSK:
                                    self.NAOSK = None
                                del self.LOOPVET[self.LOOPVET.index(sk)]
                                sk.close()
                            else:
                                self.manage(sk,data)
#-------------------------------------------------------------------------------------
#       General Exception Manegment
#-------------------------------------------------------------------------------------
        except KeyboardInterrupt:
            print("\n!> Server interrupted by user!")
            self.close()
        except Exception as e:
            print(f"\n!> Si è verificato un problema...\n{e}")
            traceback.print_exc()
            self.close()

#-------------------------------------------------------------------------------------
#                               Extra Commands
#------------------------------------------------------------------------------------- 

    def manage(self,sk,comm):
        print(f"Command {comm} Not Managed")  

#-------------------------------------------------------------------------------------
#                               Server Close
#-------------------------------------------------------------------------------------

    def close(self):
        self.BROADLOOP = False
        try:
            for sock in self.LOOPVET:
                try: sock.send(CLIENT_STOP.encode())
                except: pass
        except:
            pass
        self.SK.close()
        self.LOOPVET = []

#-------------------------------------------------------------------------------------
#                         Server Connection Manegment
#-------------------------------------------------------------------------------------

    def connectReq(self):
        sockfd, addr = self.SK.accept()
        thereis = False
        for i in range(1,len(self.LOOPVET)):
            if self.LOOPVET[i].getpeername()[0] == addr[0]:
                if self.LOOPVET[i] == self.NAOSK: #Aggiorno anche il socket Salvato per nao se nao riavvia la connessione al socket
                    self.NAOSK = sockfd
                self.LOOPVET[i] = sockfd
                thereis = True
                if sockfd.recv(RECV).decode() == CHK_COMMAND:
                    sockfd.send(ALL_DONE.encode())
                else:
                    sockfd.send(ALL_BAD.encode())
                    del self.LOOPVET[i]
                break

        if not thereis:
            self.addConnection(sockfd)

#-------------------------------------------------------------------------------------
#                            Add connection control
#-------------------------------------------------------------------------------------

    def addConnection(self,sock):
        data = sock.recv(RECV).decode()
        if data == CHK_COMMAND:
            sock.send(PROTOCOL_NAME.encode())
            data = sock.recv(RECV).decode()
            if data == SUB_COMM:
                sock.send(SUB_SUCCESS.encode())
                self.LOOPVET.append(sock)
                print(f"@> {sock.getsockname()[0]} Connected!")
            else:
                sock.send(SUB_ERROR.encode())

#-------------------------------------------------------------------------------------
#                               Get IP Function
#-------------------------------------------------------------------------------------
        
def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # doesn't even have to be reachable
        s.connect(('1.1.1.1', 1))
        IP = s.getsockname()[0]
    except:
        IP = socket.gethostbyname(socket.gethostname())
    finally:
        s.close()
    return IP 

