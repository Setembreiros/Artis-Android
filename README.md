## COGNITO

Para que o login en a creación de conta poida funcionar é preciso en gadir 4 variables no ficheiro  
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