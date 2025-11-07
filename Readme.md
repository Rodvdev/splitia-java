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

## Planes de Suscripción

Splitia ofrece tres planes de suscripción con diferentes características y límites:

### Plan FREE (Gratuito)
- **Precio:** $0.00/mes
- **Grupos:** 1 grupo máximo
- **Miembros por grupo:** 5 miembros máximo
- **Solicitudes IA/mes:** 10 solicitudes
- **Gastos por grupo:** 50 gastos máximo
- **Presupuestos por grupo:** 3 presupuestos máximo
- **Kanban:** ❌ No disponible
- **Análisis avanzados:** ❌ No disponible
- **Exportación de datos:** ❌ No disponible
- **Soporte prioritario:** ❌ No disponible

### Plan PRO ($9.99/mes)
- **Precio:** $9.99/mes
- **Grupos:** 10 grupos máximo
- **Miembros por grupo:** 50 miembros máximo
- **Solicitudes IA/mes:** 500 solicitudes
- **Gastos por grupo:** 1,000 gastos máximo
- **Presupuestos por grupo:** 50 presupuestos máximo
- **Kanban:** ✅ Disponible
- **Análisis avanzados:** ✅ Disponible
- **Exportación de datos:** ✅ Disponible
- **Soporte prioritario:** ❌ No disponible

### Plan ENTERPRISE ($29.99/mes)
- **Precio:** $29.99/mes
- **Grupos:** Ilimitado
- **Miembros por grupo:** Ilimitado
- **Solicitudes IA/mes:** Ilimitado
- **Gastos por grupo:** Ilimitado
- **Presupuestos por grupo:** Ilimitado
- **Kanban:** ✅ Disponible
- **Análisis avanzados:** ✅ Disponible
- **Exportación de datos:** ✅ Disponible
- **Soporte prioritario:** ✅ Disponible

### Gestión de Suscripciones

Los usuarios pueden gestionar sus suscripciones mediante los endpoints `/api/subscriptions`:

- **Crear suscripción:** `POST /api/subscriptions`
- **Ver suscripción actual:** `GET /api/subscriptions/current`
- **Listar todas las suscripciones:** `GET /api/subscriptions`
- **Actualizar suscripción:** `PUT /api/subscriptions/{id}`
- **Cancelar suscripción:** `DELETE /api/subscriptions/{id}` (soft delete)

### Validación de Límites

El sistema valida automáticamente los límites del plan antes de permitir operaciones:

- **Crear grupos:** Verifica límite de grupos del plan
- **Agregar miembros:** Verifica límite de miembros por grupo
- **Usar IA:** Verifica límite de solicitudes mensuales
- **Crear gastos:** Verifica límite de gastos por grupo
- **Crear presupuestos:** Verifica límite de presupuestos por grupo
- **Acceder a Kanban:** Verifica que el plan tenga acceso a Kanban

Si un usuario intenta realizar una operación que excede su límite, recibirá un error `BadRequestException` con un mensaje descriptivo indicando el límite alcanzado y sugiriendo actualizar el plan.

### Usuarios de Prueba PRO

El sistema incluye 3 usuarios PRO de prueba creados automáticamente:

1. **rodrigo@splitia.com** (password: `splitia123`)
   - Plan: PRO
   - Rol en grupo "Diseño de Software": ADMIN

2. **luis@splitia.com** (password: `splitia123`)
   - Plan: PRO
   - Rol en grupo "Diseño de Software": MEMBER

3. **israel@splitia.com** (password: `splitia123`)
   - Plan: PRO
   - Rol en grupo "Diseño de Software": MEMBER

Estos usuarios tienen acceso completo a todas las funcionalidades PRO, incluyendo Kanban, y están pre-configurados en un grupo compartido llamado "Diseño de Software".

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

#### Manejo de Autenticación y Tokens

El backend utiliza JWT (JSON Web Tokens) para la autenticación. Para mantener la sesión del usuario después de recargar la página, el frontend debe:

**1. Guardar el token después del login:**

```javascript
// Después de un login exitoso
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});

const data = await response.json();
const token = data.data.token; // Token JWT
const refreshToken = data.data.refreshToken; // Refresh token

// Guardar en localStorage o sessionStorage
localStorage.setItem('token', token);
localStorage.setItem('refreshToken', refreshToken);
localStorage.setItem('user', JSON.stringify(data.data.user));
```

**2. Enviar el token en cada request:**

```javascript
// Interceptor o función para agregar el token a todas las requests
const token = localStorage.getItem('token');

fetch('http://localhost:8080/api/users/me', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

**3. Validar sesión al recargar la página:**

```javascript
// Al cargar la aplicación, verificar si hay un token guardado
const token = localStorage.getItem('token');

if (token) {
  // Validar el token llamando a /api/auth/me
  try {
    const response = await fetch('http://localhost:8080/api/auth/me', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.ok) {
      const data = await response.json();
      // Usuario autenticado, restaurar sesión
      const user = data.data;
      // Actualizar estado de la aplicación con el usuario
    } else {
      // Token inválido o expirado, limpiar storage
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      // Redirigir a login
    }
  } catch (error) {
    // Error de red, limpiar storage
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }
}
```

**4. Formato del header Authorization:**

Todas las requests autenticadas deben incluir el header:
```
Authorization: Bearer <token>
```

**5. Respuesta del login:**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": "uuid",
      "name": "Nombre",
      "lastName": "Apellido",
      "email": "email@example.com",
      "role": "USER" | "ADMIN",
      "currency": "PEN",
      "language": "es",
      ...
    }
  },
  "message": "Login successful",
  "timestamp": "2025-11-07T10:36:24.029891"
}
```

**Nota:** El token tiene una expiración configurada (por defecto 15 minutos). El refresh token tiene una expiración más larga (por defecto 7 días). El endpoint `/api/auth/refresh` puede usarse para obtener un nuevo token cuando el actual expire.

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
- `DELETE /api/groups/{id}` - Eliminar grupo (soft delete)
- `POST /api/groups/{id}/members` - Agregar miembro al grupo (query param: userId, requiere ser admin del grupo o ADMIN)
- `DELETE /api/groups/{id}/members/{userId}` - Eliminar miembro del grupo (soft delete, requiere ser admin del grupo o ADMIN)
- `PUT /api/groups/{id}/members/{userId}` - Actualizar rol y permisos del miembro (requiere ser admin del grupo o ADMIN)
- `PUT /api/groups/{id}/members/{userId}/permissions` - Asignar permisos granulares al miembro (requiere ser admin del grupo o ADMIN)

### Gastos (`/api/expenses`)
- `GET /api/expenses` - Listar gastos (query param opcional: groupId, paginado)
- `POST /api/expenses` - Crear nuevo gasto
- `GET /api/expenses/{id}` - Obtener gasto por ID
- `PUT /api/expenses/{id}` - Actualizar gasto
- `DELETE /api/expenses/{id}` - Eliminar gasto (soft delete)

### Presupuestos (`/api/budgets`)
- `GET /api/budgets` - Listar presupuestos del usuario (paginado)
- `POST /api/budgets` - Crear nuevo presupuesto
- `GET /api/budgets/{id}` - Obtener presupuesto por ID
- `PUT /api/budgets/{id}` - Actualizar presupuesto
- `DELETE /api/budgets/{id}` - Eliminar presupuesto (soft delete)

### Categorías (`/api/categories`)
- `GET /api/categories` - Obtener categorías del usuario (paginado)
- `GET /api/categories/{id}` - Obtener categoría por ID
- `POST /api/categories` - Crear nueva categoría
- `PUT /api/categories/{id}` - Actualizar categoría
- `DELETE /api/categories/{id}` - Eliminar categoría (soft delete)

### Chat (`/api/conversations`)
- `GET /api/conversations` - Listar conversaciones del usuario (paginado)
- `POST /api/conversations` - Crear nueva conversación
- `GET /api/conversations/{id}` - Obtener conversación por ID
- `PUT /api/conversations/{id}` - Actualizar conversación
- `DELETE /api/conversations/{id}` - Eliminar conversación (soft delete)
- `POST /api/conversations/{conversationId}/messages` - Enviar mensaje en conversación
- `GET /api/conversations/{conversationId}/messages` - Obtener mensajes de conversación (paginado)
- `PUT /api/conversations/messages/{id}` - Actualizar mensaje
- `DELETE /api/conversations/messages/{id}` - Eliminar mensaje (soft delete)

### Liquidaciones (`/api/settlements`)
- `GET /api/settlements` - Listar liquidaciones de un grupo (query param: groupId, paginado)
- `POST /api/settlements` - Crear nueva liquidación
- `GET /api/settlements/{id}` - Obtener liquidación por ID
- `PUT /api/settlements/{id}` - Actualizar liquidación (Admin only)
- `DELETE /api/settlements/{id}` - Eliminar liquidación (soft delete, Admin only)

### Suscripciones (`/api/subscriptions`)
- `GET /api/subscriptions` - Listar suscripciones del usuario (paginado)
- `POST /api/subscriptions` - Crear nueva suscripción
- `GET /api/subscriptions/{id}` - Obtener suscripción por ID
- `GET /api/subscriptions/current` - Obtener suscripción actual activa del usuario
- `PUT /api/subscriptions/{id}` - Actualizar suscripción
- `DELETE /api/subscriptions/{id}` - Eliminar suscripción (soft delete)

### Soporte (`/api/support`)
- `GET /api/support/tickets` - Listar tickets de soporte del usuario (paginado)
- `POST /api/support/tickets` - Crear ticket de soporte
- `GET /api/support/tickets/{id}` - Obtener ticket de soporte por ID
- `PUT /api/support/tickets/{id}` - Actualizar ticket (Admin only)
- `DELETE /api/support/tickets/{id}` - Eliminar ticket (soft delete, Admin only)

### Tareas Kanban (`/api/tasks`)
- `GET /api/tasks/group/{groupId}` - Listar tareas del grupo (paginado)
- `GET /api/tasks/group/{groupId}/status/{status}` - Tareas por estado (para columnas Kanban: TODO, DOING, DONE)
- `GET /api/tasks/{id}` - Obtener tarea por ID
- `POST /api/tasks` - Crear nueva tarea (requiere plan PRO o ENTERPRISE)
- `PUT /api/tasks/{id}` - Actualizar tarea
- `DELETE /api/tasks/{id}` - Eliminar tarea (soft delete)

**Nota:** El acceso a Kanban está disponible solo para usuarios con plan PRO o ENTERPRISE.

### Etiquetas de Tareas (`/api/task-tags`)
- `GET /api/task-tags/group/{groupId}` - Listar etiquetas del grupo
- `GET /api/task-tags/{id}` - Obtener etiqueta por ID
- `POST /api/task-tags` - Crear nueva etiqueta (requiere plan PRO o ENTERPRISE)
- `PUT /api/task-tags/{id}` - Actualizar etiqueta
- `DELETE /api/task-tags/{id}` - Eliminar etiqueta (soft delete, solo admins del grupo)

**Nota:** Las etiquetas de tareas están disponibles solo para usuarios con plan PRO o ENTERPRISE.

### IA (`/api/ai`)
- `POST /api/ai/process-message` - Procesar mensaje con IA

**Nota:** Los límites de solicitudes de IA varían según el plan del usuario (ver sección de Planes).

#### Notas Importantes sobre los Endpoints

**Soft Delete:**
- Todos los endpoints `DELETE` para usuarios regulares realizan **soft delete** por defecto
- El soft delete marca el registro con `deletedAt` pero no lo elimina físicamente de la base de datos
- Los registros eliminados (soft delete) no aparecen en las consultas GET normales
- Solo los administradores pueden realizar **hard delete** (eliminación física permanente) usando los endpoints `/api/admin` con el parámetro `?hard=true`

**Paginación:**
- Todos los endpoints GET que retornan listas soportan paginación mediante parámetros de query:
  - `page`: Número de página (por defecto: 0)
  - `size`: Tamaño de página (por defecto: 20)
  - `sort`: Campo(s) para ordenar (ejemplo: `sort=createdAt,desc`)
- Ejemplo: `GET /api/categories?page=0&size=10&sort=createdAt,desc`

**Permisos y Roles:**
- Los usuarios regulares solo pueden modificar sus propios recursos
- Los administradores de grupo (`GroupRole.ADMIN`) pueden gestionar miembros y permisos dentro de sus grupos
- Los administradores del sistema (`UserRole.ADMIN`) tienen acceso completo a todos los recursos mediante `/api/admin`
- Los permisos granulares en grupos se almacenan en formato JSONB y pueden incluir permisos personalizados como `canEditExpenses`, `canDeleteExpenses`, etc.

**Planes y Límites:**
- Los usuarios tienen límites según su plan de suscripción (FREE, PRO, ENTERPRISE)
- Algunas funcionalidades como Kanban están disponibles solo para planes PRO y ENTERPRISE
- Los límites se validan automáticamente antes de permitir operaciones

### Administración (`/api/admin`)

**Usuarios:**
- `GET /api/admin/users` - Obtener todos los usuarios (paginado)
- `GET /api/admin/users/{id}` - Obtener usuario por ID
- `DELETE /api/admin/users/{id}` - Eliminar usuario

**Grupos:**
- `GET /api/admin/groups` - Obtener todos los grupos (paginado)
- `GET /api/admin/groups/{id}` - Obtener grupo por ID
- `DELETE /api/admin/groups/{id}` - Eliminar grupo

**Gastos:**
- `GET /api/admin/expenses` - Obtener todos los gastos (paginado)
- `GET /api/admin/expenses/{id}` - Obtener gasto por ID
- `DELETE /api/admin/expenses/{id}` - Eliminar gasto

**Participaciones en Gastos:**
- `GET /api/admin/expense-shares` - Obtener todas las participaciones (paginado)
- `GET /api/admin/expense-shares/{id}` - Obtener participación por ID
- `DELETE /api/admin/expense-shares/{id}` - Eliminar participación

**Presupuestos:**
- `GET /api/admin/budgets` - Obtener todos los presupuestos (paginado)
- `GET /api/admin/budgets/{id}` - Obtener presupuesto por ID
- `DELETE /api/admin/budgets/{id}` - Eliminar presupuesto

**Categorías:**
- `GET /api/admin/categories` - Obtener todas las categorías (paginado)
- `GET /api/admin/categories/{id}` - Obtener categoría por ID
- `DELETE /api/admin/categories/{id}` - Eliminar categoría

**Conversaciones:**
- `GET /api/admin/conversations` - Obtener todas las conversaciones (paginado)
- `GET /api/admin/conversations/{id}` - Obtener conversación por ID
- `DELETE /api/admin/conversations/{id}` - Eliminar conversación

**Mensajes:**
- `GET /api/admin/messages` - Obtener todos los mensajes (paginado)
- `GET /api/admin/messages/{id}` - Obtener mensaje por ID
- `DELETE /api/admin/messages/{id}` - Eliminar mensaje

**Liquidaciones:**
- `GET /api/admin/settlements` - Obtener todas las liquidaciones (paginado)
- `GET /api/admin/settlements/{id}` - Obtener liquidación por ID
- `DELETE /api/admin/settlements/{id}` - Eliminar liquidación

**Suscripciones:**
- `GET /api/admin/subscriptions` - Obtener todas las suscripciones (paginado)
- `GET /api/admin/subscriptions/{id}` - Obtener suscripción por ID
- `DELETE /api/admin/subscriptions/{id}` - Eliminar suscripción

**Tickets de Soporte:**
- `GET /api/admin/support-tickets` - Obtener todos los tickets (paginado)
- `GET /api/admin/support-tickets/{id}` - Obtener ticket por ID
- `DELETE /api/admin/support-tickets/{id}` - Eliminar ticket

**Invitaciones a Grupos:**
- `GET /api/admin/group-invitations` - Obtener todas las invitaciones (paginado)
- `GET /api/admin/group-invitations/{id}` - Obtener invitación por ID
- `DELETE /api/admin/group-invitations/{id}` - Eliminar invitación

**Usuarios de Grupos:**
- `GET /api/admin/group-users` - Obtener todas las relaciones grupo-usuario (paginado)
- `GET /api/admin/group-users/{id}` - Obtener relación por ID
- `DELETE /api/admin/group-users/{id}` - Eliminar relación

**Nota:** Todas las rutas de administración requieren autenticación y rol de administrador. Los endpoints de listado soportan paginación mediante parámetros `page`, `size` y `sort`.

**Eliminación (DELETE):**
- Por defecto, los endpoints DELETE realizan **soft delete** (marca `deletedAt` sin eliminar físicamente)
- Para realizar **hard delete** (eliminación física permanente), agregar el parámetro `?hard=true`
- Ejemplo: `DELETE /api/admin/users/{id}?hard=true` - Elimina permanentemente el usuario

**Ejemplos:**
```bash
# Soft delete (por defecto)
DELETE /api/admin/users/123e4567-e89b-12d3-a456-426614174000

# Hard delete (eliminación permanente)
DELETE /api/admin/users/123e4567-e89b-12d3-a456-426614174000?hard=true
```

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

## Sistema Kanban

Splitia incluye un sistema completo de gestión de tareas tipo Kanban para grupos, disponible para usuarios con plan PRO o ENTERPRISE.

### Características del Kanban

- **Tres columnas:** TODO, DOING, DONE
- **Asignación de responsables:** Cada tarea puede asignarse a un miembro del grupo
- **Fechas:** Fecha de inicio y fecha de vencimiento
- **Prioridades:** LOW, MEDIUM, HIGH, URGENT
- **Etiquetas:** Etiquetas personalizadas por grupo con colores
- **Ordenamiento:** Posición personalizable dentro de cada columna

### Uso del Kanban

#### Obtener tareas por estado (para columnas Kanban)

```bash
# Obtener tareas en estado TODO
GET /api/tasks/group/{groupId}/status/TODO

# Obtener tareas en estado DOING
GET /api/tasks/group/{groupId}/status/DOING

# Obtener tareas en estado DONE
GET /api/tasks/group/{groupId}/status/DONE
```

#### Crear una tarea

```bash
POST /api/tasks
{
  "title": "Implementar login",
  "description": "Crear sistema de autenticación con JWT",
  "groupId": "uuid-del-grupo",
  "assignedToId": "uuid-del-usuario",
  "priority": "HIGH",
  "startDate": "2025-11-01",
  "dueDate": "2025-12-31",
  "tagIds": ["uuid-tag-1", "uuid-tag-2"]
}
```

#### Mover tarea entre columnas

```bash
PUT /api/tasks/{taskId}
{
  "status": "DOING",
  "position": 0
}
```

#### Crear etiqueta

```bash
POST /api/task-tags
{
  "name": "Urgente",
  "color": "#FF0000",
  "groupId": "uuid-del-grupo"
}
```

### Permisos del Kanban

- **Crear/Editar tareas:** Cualquier miembro del grupo
- **Eliminar tareas:** Solo el creador o admin del grupo
- **Crear etiquetas:** Cualquier miembro del grupo
- **Eliminar etiquetas:** Solo admins del grupo

## Próximos Pasos

1. ✅ Sistema de planes de suscripción (FREE, PRO, ENTERPRISE)
2. ✅ Sistema Kanban de tareas
3. Implementar lógica completa de cálculo de balances
4. Integrar servicio de IA (OpenAI/Claude)
5. Integrar Stripe para pagos de suscripciones
6. Implementar WebSocket para chat en tiempo real
7. Agregar más tests de integración
8. Optimizar consultas con índices adicionales
9. Implementar caching con Redis
10. Configurar CI/CD pipeline
11. Notificaciones para tareas próximas a vencer
12. Reportes y analytics de productividad por grupo

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

**Versión**: 2.0.0  
**Última actualización**: Noviembre 2025

---

## 📚 Documentación Adicional

Para más detalles sobre las actualizaciones recientes, consulta el archivo [ACTUALIZACIONES.md](./ACTUALIZACIONES.md) que incluye:

- Detalles completos del sistema de planes de suscripción
- Documentación completa del sistema Kanban
- Lista de archivos modificados y creados
- Guías de uso y ejemplos
