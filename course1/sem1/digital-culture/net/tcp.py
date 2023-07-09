import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('10.8.0.1', 1234))
print(s.recv(1024))
print(s.recv(2**10))
print(s.recv(2**10))
print(s.recv(2**10))
for i in range(1, 61):
    a = str(eval(s.recv(2 ** 10))).encode()
    print(a)
    s.send(a)
    s.recv(2**10)
print(s.recv(2**10))