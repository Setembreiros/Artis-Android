## COGNITO

Para que o login en a creación de conta poida funcionar é preciso engadir 4 variables no ficheiro  
secret.properties. Estas claves saen de AWS.  
O fichero local.properties está no .gitignore. Este ficheiro baixo ningún concepto pódese subir a  
github.

O ficheiro quedaria asi
```kotlin
CLIENT_ID_UA = "*********************************"  
CLIENT_ID_UE = "*********************************"  
  
SECRET_KEY_UA = "***************************************************************"  
SECRET_KEY_UE = "***************************************************************"
```

## Local configuration

Engadir no ficheiro local.properties as seguintes lineas

```kotlin
API_URL_DEBUG= "http://192.168.1.39:5555/development/" -> Cambiar 192.168.1.39 por IP local, no serve local host nin 127.0.0.1
API_URL_RELEASE= "http://localhost:5555/production/" -> Cando teñamos api en producción modificar!
S3_URL_DEBUG= "http://192.168.1.44:4566/"
```

No directorio res->xml modificar o ficheiro network_security_config.xml cambiando api local pola do teu ordenador.