# Splitia Backend

Backend API para Splitia - Plataforma de gestión de gastos compartidos desarrollada con Spring Boot.

## Descripción

Splitia es una aplicación web completa para la gestión de gastos compartidos que permite a usuarios individuales y grupos dividir gastos de manera eficiente, realizar seguimiento de presupuestos, comunicarse mediante chat integrado, y utilizar un asistente de IA para automatizar tareas financieras.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Para acceso a datos
- **PostgreSQL** - Base de datos
- **Spring Security + JWT** - Autenticación y autorización
- **Flyway** - Migraciones de base de datos
- **MapStruct** - Mapeo de DTOs
- **Lombok** - Reducción de boilerplate
- **Swagger/OpenAPI** - Documentación de API
- **Maven** - Herramienta de build

## Requisitos Previos

- **Java 17** (requerido - ver nota importante abajo)
- Maven 3.6+
- PostgreSQL 12+ o Neon PostgreSQL
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### ⚠️ Nota Importante sobre Java

**Este proyecto requiere Java 17 específicamente.** Java 25 y versiones más recientes no son compatibles con Lombok y MapStruct debido a cambios en las APIs internas del compilador.

Si tienes múltiples versiones de Java instaladas, asegúrate de usar Java 17 para compilar y ejecutar este proyecto.

## Configuración

### 1. Clonar el Repositorio

#### Opción A: Clonar desde GitHub (si el repositorio está en GitHub)

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/splitia-java.git

# O usando SSH (si tienes configuradas las claves SSH)
git clone git@github.com:tu-usuario/splitia-java.git

# Navegar al directorio del proyecto
cd splitia-java
```

#### Opción B: Clonar desde otro repositorio Git

```bash
# Reemplaza <repository-url> con la URL de tu repositorio
git clone <repository-url>
cd splitia-java
```

#### Opción C: Descargar como ZIP

1. Descarga el código fuente como archivo ZIP desde tu repositorio
2. Extrae el archivo ZIP
3. Navega al directorio extraído:
   ```bash
cd splitia-java
   ```

#### Opción D: Trabajar con un repositorio existente

Si ya tienes el código localmente:

   ```bash
# Navegar al directorio del proyecto
cd splitia-java

# Si es un repositorio Git, actualizar a la última versión
git pull origin main

# O si estás en otra rama
git pull origin develop
```

### 2. Verificar Requisitos Previos

Antes de continuar, verifica que tienes todos los requisitos instalados:

#### Verificar Java 17

```bash
# Verificar versión de Java
java -version

# Debería mostrar algo como: openjdk version "17.0.x"
# Si muestra Java 25 o superior, necesitas instalar Java 17
```

#### Verificar Maven

```bash
# Verificar versión de Maven
mvn -version

# Debería mostrar Apache Maven 3.6.x o superior
```

#### Verificar Git (opcional, solo si vas a clonar)

```bash
# Verificar versión de Git
git --version

# Debería mostrar git version 2.x.x o superior
```

### 3. Configurar Base de Datos

#### Opción A: Base de Datos Local PostgreSQL

##### Instalación de PostgreSQL

**macOS (con Homebrew):**
```bash
# Instalar PostgreSQL
brew install postgresql@15

# Iniciar el servicio
brew services start postgresql@15

# O iniciar manualmente
pg_ctl -D /opt/homebrew/var/postgresql@15 start
```

**Linux (Ubuntu/Debian):**
```bash
# Actualizar paquetes
sudo apt update

# Instalar PostgreSQL
sudo apt install postgresql postgresql-contrib

# Iniciar el servicio
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Linux (Fedora/RHEL/CentOS):**
```bash
# Instalar PostgreSQL
sudo dnf install postgresql-server postgresql-contrib

# Inicializar la base de datos
sudo postgresql-setup --initdb

# Iniciar el servicio
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Windows:**
1. Descarga PostgreSQL desde [postgresql.org](https://www.postgresql.org/download/windows/)
2. Ejecuta el instalador y sigue las instrucciones
3. Durante la instalación, configura una contraseña para el usuario `postgres`
4. PostgreSQL se iniciará automáticamente como servicio

##### Crear Base de Datos

Una vez que PostgreSQL esté instalado y ejecutándose:

```bash
# Conectarse a PostgreSQL (macOS/Linux)
psql postgres

# O en Windows, usar pgAdmin o psql desde la línea de comandos
```

Luego ejecuta estos comandos SQL:

```sql
-- Crear base de datos
CREATE DATABASE splitia;

-- Crear usuario (si no existe)
CREATE USER postgres WITH PASSWORD 'postgres';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE splitia TO postgres;

-- Conectarse a la base de datos
\c splitia

-- Otorgar privilegios en el esquema público
GRANT ALL ON SCHEMA public TO postgres;

-- Salir de psql
\q
```

##### Configurar application.properties para Base de Datos Local

Edita `src/main/resources/application.properties`:

```properties
# Database - PostgreSQL Local
spring.datasource.url=jdbc:postgresql://localhost:5432/splitia
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

#### Opción B: Neon PostgreSQL (Cloud)

El proyecto está configurado para usar Neon PostgreSQL. La configuración ya está establecida en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://ep-dawn-frost-a5v2c5t1-pooler.us-east-2.aws.neon.tech:5432/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_GFt8oBh1uUQM
```

**Nota de Seguridad**: Para producción, se recomienda usar variables de entorno en lugar de credenciales hardcodeadas.

##### Cómo obtener la URL de conexión de Neon:

1. Accede a tu proyecto en [Neon Console](https://console.neon.tech)
2. Ve a la sección "Connection Details"
3. Copia la connection string que tiene el formato:
   ```
   postgres://username:password@host:port/database?sslmode=require
   ```
4. Convierte la URL a formato JDBC:
   - De: `postgres://user:pass@host:port/db?sslmode=require`
   - A: `jdbc:postgresql://host:port/db?sslmode=require`
   - Username: `user`
   - Password: `pass`

##### Actualizar la configuración:

Edita `src/main/resources/application.properties` y actualiza:
- `spring.datasource.url` con la URL JDBC
- `spring.datasource.username` con tu usuario
- `spring.datasource.password` con tu contraseña

### 4. Configurar Variables de Entorno (Recomendado para Producción)

Para usar variables de entorno, puedes crear un archivo `.env` o establecerlas en tu sistema:

```bash
export DATABASE_URL="postgres://neondb_owner:npg_GFt8oBh1uUQM@ep-dawn-frost-a5v2c5t1-pooler.us-east-2.aws.neon.tech:5432/neondb?sslmode=require"
export JWT_SECRET="your-secret-key-change-in-production-min-256-bits"
```

Luego editar `application.properties` para usar variables de entorno (ver `application.properties.example`).

### 5. Ejecutar Migraciones

Las migraciones de Flyway se ejecutarán automáticamente al iniciar la aplicación. Para ejecutarlas manualmente:

```bash
mvn flyway:migrate
```

## Configuración Inicial Completa (Guía Paso a Paso)

### Para Usuarios Nuevos - Setup Completo

#### Paso 1: Clonar o Descargar el Proyecto

```bash
# Si tienes acceso al repositorio Git
git clone <repository-url>
cd splitia-java

# O descarga y extrae el ZIP, luego:
cd splitia-java
```

#### Paso 2: Instalar Java 17

**macOS:**
```bash
# Instalar Java 17
brew install openjdk@17

# Configurar JAVA_HOME para esta sesión
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# Verificar instalación
$JAVA_HOME/bin/java -version
```

**Linux (Ubuntu/Debian):**
```bash
# Instalar OpenJDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# Verificar instalación
java -version

# Configurar JAVA_HOME (agregar a ~/.bashrc o ~/.zshrc)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

**Linux (Fedora/RHEL/CentOS):**
```bash
# Instalar OpenJDK 17
sudo dnf install java-17-openjdk-devel

# Verificar instalación
java -version

# Configurar JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

**Windows:**
1. Descarga OpenJDK 17 desde [adoptium.net](https://adoptium.net/)
2. Instala el JDK
3. Configura la variable de entorno `JAVA_HOME`:
   - Panel de Control → Sistema → Configuración avanzada del sistema
   - Variables de entorno → Nueva variable del sistema
   - Nombre: `JAVA_HOME`
   - Valor: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot` (ajusta la ruta)
4. Agrega `%JAVA_HOME%\bin` al PATH

#### Paso 3: Instalar Maven

**macOS:**
```bash
brew install maven
mvn -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
mvn -version
```

**Linux (Fedora/RHEL/CentOS):**
```bash
sudo dnf install maven
mvn -version
```

**Windows:**
1. Descarga Maven desde [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extrae el archivo ZIP
3. Configura `MAVEN_HOME` y agrega `%MAVEN_HOME%\bin` al PATH

#### Paso 4: Configurar Base de Datos

Elige una opción:

**A) Usar PostgreSQL Local** (ver sección anterior)
**B) Usar Neon PostgreSQL** (ya configurado por defecto)

#### Paso 5: Compilar el Proyecto

```bash
# Asegúrate de estar en el directorio del proyecto
cd splitia-java

# Configurar Java 17 (si no está configurado permanentemente)
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home  # macOS
# O la ruta correspondiente para tu sistema

# Compilar el proyecto
mvn clean install

# Si todo está bien, deberías ver: BUILD SUCCESS
```

#### Paso 6: Ejecutar la Aplicación

```bash
# Opción más fácil - usar el script
./run.sh

# O manualmente
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

#### Paso 7: Verificar que Funciona

```bash
# En otra terminal, verificar health check
curl http://localhost:8080/actuator/health

# Debería devolver: {"status":"UP"}

# Abrir Swagger UI en el navegador
open http://localhost:8080/swagger-ui.html  # macOS
# O visitar manualmente: http://localhost:8080/swagger-ui.html
```

## Compilación y Ejecución

### Verificar Versión de Java

Primero, verifica qué versión de Java estás usando:

```bash
java -version
```

Si muestra Java 25 o superior, necesitas cambiar a Java 17.

### Configurar Java 17

#### En macOS (con Homebrew):

1. Verificar si Java 17 está instalado:
```bash
brew list openjdk@17
```

2. Si no está instalado, instálalo:
```bash
brew install openjdk@17
```

3. Configurar JAVA_HOME para esta sesión:
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
```

4. Verificar que Java 17 está activo:
```bash
$JAVA_HOME/bin/java -version
# Debería mostrar: openjdk version "17.0.x"
```

#### Configuración Permanente (macOS):

Agrega esto a tu `~/.zshrc` o `~/.bash_profile`:

```bash
# Java 17 para Splitia
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

Luego recarga tu shell:
```bash
source ~/.zshrc  # o source ~/.bash_profile
```

### Compilar el Proyecto

Con Java 17 configurado, compila el proyecto:

```bash
mvn clean install
```

Si encuentras errores de compilación relacionados con Lombok o MapStruct, asegúrate de estar usando Java 17.

### Ejecutar la Aplicación

#### Opción 1: Usando el Script run.sh (Más Fácil)

```bash
./run.sh
```

Este script automáticamente configura Java 17 y ejecuta la aplicación.

#### Opción 2: Usando Maven Directamente

```bash
# Asegúrate de tener JAVA_HOME configurado para Java 17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

**Nota**: El comando correcto es `mvn spring-boot:run`, no `mvn run`.

#### Opción 3: Ejecutar el JAR

Primero compila el proyecto, luego ejecuta el JAR:

```bash
mvn clean package
java -jar target/splitia-backend-1.0.0.jar
```

### Verificar que la Aplicación Está Ejecutándose

Una vez iniciada, la aplicación estará disponible en: `http://localhost:8080`

Puedes verificar el estado con:

```bash
# Health check (debería devolver {"status":"UP"})
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html

# O verificar que el puerto está en uso
lsof -ti:8080
```

Si ves `{"status":"UP"}`, la aplicación está funcionando correctamente.

### Solución de Problemas

#### Error: "ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN"

Este error indica que estás usando Java 25 o superior. **Solución**: Cambia a Java 17 usando los pasos anteriores.

#### Error: "cannot find symbol" en clases con @Data o @Getter/@Setter

Esto indica que Lombok no está generando los métodos. **Solución**: 
1. Asegúrate de usar Java 17
2. Verifica que Lombok está en las dependencias del `pom.xml`
3. Limpia y recompila: `mvn clean compile`

#### Error de conexión a la base de datos

**Si usas Neon PostgreSQL:**
Verifica que:
1. La base de datos Neon está accesible
2. Las credenciales en `application.properties` son correctas
3. El SSL está habilitado (`sslmode=require`)
4. Tu IP no está bloqueada por el firewall de Neon

**Si usas PostgreSQL Local:**
Verifica que:
1. PostgreSQL está ejecutándose:
   ```bash
   # macOS
   brew services list | grep postgresql
   
   # Linux
   sudo systemctl status postgresql
   
   # Windows
   # Verificar en Servicios de Windows
   ```

2. Puedes conectarte a PostgreSQL:
   ```bash
   psql -U postgres -d splitia
   ```

3. Las credenciales en `application.properties` coinciden con tu configuración local

4. El puerto 5432 está disponible:
   ```bash
   # macOS/Linux
   lsof -i :5432
   
   # Windows
   netstat -an | findstr 5432
   ```

#### Error: "Port 8080 already in use"

La aplicación no puede iniciar porque el puerto 8080 está ocupado. Soluciones:

```bash
# Encontrar qué proceso usa el puerto 8080
# macOS/Linux
lsof -ti:8080

# Matar el proceso
kill -9 $(lsof -ti:8080)

# O cambiar el puerto en application.properties
server.port=8081
```

#### Error: "Cannot find symbol" o errores de compilación

1. Limpia y recompila:
   ```bash
   mvn clean compile
   ```

2. Verifica que estás usando Java 17:
   ```bash
   java -version
   ```

3. Verifica que Lombok está funcionando (en tu IDE, asegúrate de tener el plugin de Lombok instalado)

#### Error: "Failed to execute goal" durante mvn install

1. Verifica tu conexión a Internet (Maven necesita descargar dependencias)
2. Verifica que Maven puede acceder a los repositorios
3. Intenta con:
   ```bash
   mvn clean install -U
   ```
   El flag `-U` fuerza la actualización de dependencias

## Documentación de API

Una vez que la aplicación esté ejecutándose, accede a la documentación Swagger en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/splitia/
│   │   ├── model/              # Entidades JPA
│   │   ├── repository/         # Repositorios Spring Data
│   │   ├── service/            # Lógica de negocio
│   │   ├── controller/         # Controladores REST
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── mapper/             # MapStruct mappers
│   │   ├── security/           # Configuración de seguridad
│   │   ├── exception/          # Manejo de excepciones
│   │   ├── config/             # Configuraciones
│   │   └── util/               # Utilidades
│   └── resources/
│       ├── application.properties
│       └── db/migration/       # Migraciones Flyway
└── test/
    └── java/com/splitia/       # Tests
```

## Endpoints Principales

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/refresh` - Refrescar token de acceso
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/me` - Obtener usuario actual autenticado

### Usuarios (`/api/users`)
- `GET /api/users/me` - Obtener perfil del usuario actual
- `PUT /api/users/me` - Actualizar perfil del usuario
- `PUT /api/users/me/password` - Cambiar contraseña
- `PUT /api/users/me/preferences` - Actualizar preferencias del usuario

### Grupos (`/api/groups`)
- `GET /api/groups` - Listar grupos del usuario
- `POST /api/groups` - Crear nuevo grupo
- `GET /api/groups/{id}` - Obtener grupo por ID
- `PUT /api/groups/{id}` - Actualizar grupo
- `DELETE /api/groups/{id}` - Eliminar grupo
- `POST /api/groups/{id}/members` - Agregar miembro al grupo (query param: userId)
- `DELETE /api/groups/{id}/members/{userId}` - Eliminar miembro del grupo

### Gastos (`/api/expenses`)
- `GET /api/expenses` - Listar gastos (query param opcional: groupId, paginación)
- `POST /api/expenses` - Crear nuevo gasto
- `GET /api/expenses/{id}` - Obtener gasto por ID
- `PUT /api/expenses/{id}` - Actualizar gasto
- `DELETE /api/expenses/{id}` - Eliminar gasto

### Presupuestos (`/api/budgets`)
- `POST /api/budgets` - Crear nuevo presupuesto
- `GET /api/budgets/{id}` - Obtener presupuesto por ID
- `PUT /api/budgets/{id}` - Actualizar presupuesto
- `DELETE /api/budgets/{id}` - Eliminar presupuesto

### Categorías (`/api/categories`)
- `GET /api/categories` - Obtener categorías del usuario

### Chat (`/api/conversations`)
- `POST /api/conversations/{conversationId}/messages` - Enviar mensaje en conversación
- `GET /api/conversations/{conversationId}/messages` - Obtener mensajes de conversación (paginación)

### Liquidaciones (`/api/settlements`)
- `GET /api/settlements/{id}` - Obtener liquidación por ID

### Suscripciones (`/api/subscriptions`)
- `GET /api/subscriptions/current` - Obtener suscripción actual del usuario

### Soporte (`/api/support`)
- `POST /api/support/tickets` - Crear ticket de soporte
- `GET /api/support/tickets/{id}` - Obtener ticket de soporte por ID

### IA (`/api/ai`)
- `POST /api/ai/process-message` - Procesar mensaje con IA

### Administración (`/api/admin`)
- `GET /api/admin/users` - Obtener todos los usuarios (solo administradores)

## Testing

Ejecutar todos los tests (asegúrate de usar Java 17):

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn test
```

Ejecutar tests específicos:

```bash
mvn test -Dtest=UserServiceTest
```

**Nota**: Los tests usan H2 en memoria, por lo que no requieren una base de datos PostgreSQL activa.

## Docker

### Construir y Ejecutar con Docker Compose

El proyecto incluye configuración de Docker para desarrollo local con PostgreSQL:

```bash
# Construir y ejecutar todos los servicios
docker-compose up --build

# Ejecutar en segundo plano
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (⚠️ elimina datos de la base de datos)
docker-compose down -v
```

### Construir Imagen Docker Individualmente

```bash
# Construir la imagen
docker build -t splitia-backend:latest .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/splitia \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  splitia-backend:latest
```

### Docker con Neon PostgreSQL (Producción)

Para usar Neon PostgreSQL en lugar de la base de datos local de Docker:

1. Edita `docker-compose.yml` y actualiza las variables de entorno del servicio `app`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://ep-dawn-frost-a5v2c5t1-pooler.us-east-2.aws.neon.tech:5432/neondb?sslmode=require
  SPRING_DATASOURCE_USERNAME: neondb_owner
  SPRING_DATASOURCE_PASSWORD: npg_GFt8oBh1uUQM
```

2. O crea un archivo `.env` en la raíz del proyecto:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-dawn-frost-a5v2c5t1-pooler.us-east-2.aws.neon.tech:5432/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=npg_GFt8oBh1uUQM
```

3. Ejecuta solo el servicio de la aplicación (sin PostgreSQL local):

```bash
docker-compose up app --build
```

**Nota**: Si usas Neon PostgreSQL, puedes comentar o eliminar el servicio `postgres` del `docker-compose.yml`.

## Configuración de Perfiles

### Desarrollo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Producción

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker

```bash
docker-compose up
# Usa el perfil 'docker' automáticamente
```

## Seguridad

- Las contraseñas se encriptan con BCrypt
- Los tokens JWT tienen expiración de 15 minutos
- Los refresh tokens tienen expiración de 7 días
- Todas las rutas excepto `/api/auth/**` requieren autenticación

## Guía Rápida de Inicio Rápido

Si ya tienes todo instalado y solo quieres ejecutar el proyecto:

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd splitia-java

# 2. Configurar Java 17 (si no está configurado)
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# 3. Compilar
mvn clean install

# 4. Ejecutar
./run.sh

# 5. Verificar
curl http://localhost:8080/actuator/health
```

## Próximos Pasos

1. Implementar lógica completa de cálculo de balances
2. Integrar servicio de IA (OpenAI/Claude)
3. Integrar Stripe para suscripciones
4. Implementar WebSocket para chat en tiempo real
5. Agregar más tests de integración
6. Optimizar consultas con índices adicionales
7. Implementar caching con Redis
8. Configurar CI/CD pipeline

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia Apache 2.0 - ver el archivo LICENSE para más detalles.

## Contacto

Splitia Team - support@splitia.com

---

**Versión**: 1.0.0  
**Última actualización**: 2024
