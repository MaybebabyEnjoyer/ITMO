# env_cmd

```docker
docker run --env I_NEED_KEY=[key] [container]
```

# run_cmd

```
docker desktop -> container -> terminal -> ls /var/
```

# args_cmd

```docker
docker run [container] give_args_key [key]
```

# file_cmd

```docker
docker cp [container]:/file.png [path]
```

# lost_cmd

```
docker desktop -> layers
```

# port_cmd

```docker
docker run -p [port] [container]
```

# first_cmd

```docker
docker run [container]
```

# volume_cmd
```docker
docker run -v [path] [container]
```