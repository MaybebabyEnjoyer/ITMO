```curl
curl -H "X-Access-Token: []" -H 'Content-Type: application/json' --data '{"text":"[]"}'  --request PATCH 'http://[]'
```

DNS: https://www.reg.ru/nettools/dig

UDP

1. конфигурация на windows (нужно чтобы VPN работал через WSL) 
 ```
sudo apt-install OpenVPN
```
2. Включить VPN в WSL
```
sudo openvpn config.ovpn
```
3. оставить VPN работать в бг ```ctrl + Z``` + bg
4. подключить IP к VPN
```
sudo ip addr add 10.8.0.214/24 dev tap0
```
5. слушать порт x
```
sudo nc -l -lu x
```
