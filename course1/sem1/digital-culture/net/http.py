import requests

sess = requests.session()
url = 'http://10.8.0.1/'

for i in range(1,100001):
    x = sess.post(url)
    
    print(x.text)