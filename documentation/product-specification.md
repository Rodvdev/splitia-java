# Especificación de Producto: Splitia - Monolito Java MVC

## 1. Información General del Proyecto

### 1.1 Descripción del Producto
Splitia es una plataforma web completa para la gestión de gastos compartidos que permite a usuarios individuales y grupos dividir gastos de manera eficiente, realizar seguimiento de presupuestos, comunicarse mediante chat integrado, y utilizar un asistente de IA para automatizar tareas financieras.

### 1.2 Objetivo del Documento
Esta especificación detalla los requerimientos funcionales y técnicos para la implementación de Splitia como un monolito Java utilizando arquitectura MVC (Model-View-Controller) con capas de Repository y Services.

### 1.3 Alcance
- Sistema completo de gestión de gastos individuales y grupales
- Sistema de grupos con roles y permisos
- Sistema de presupuestos mensuales
- Sistema de chat (individual y grupal)
- Asistente de IA integrado
- Sistema de suscripciones y facturación
- Sistema de soporte con tickets
- Panel administrativo completo
- API RESTful completa
- Autenticación y autorización

### 1.4 Tecnologías Principales
- **Backend**: Java (Spring Boot)
- **Base de Datos**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Arquitectura**: MVC (Model-View-Controller)
- **Autenticación**: Spring Security + JWT
- **API**: RESTful
- **Build Tool**: Maven o Gradle
- **Testing**: JUnit, Mockito

---

## 2. Arquitectura del Sistema

### 2.1 Estructura MVC

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── splitia/
│   │           ├── SplitiaApplication.java
│   │           │
│   │           ├── model/              # Entidades JPA
│   │           │   ├── User.java
│   │           │   ├── Group.java
│   │           │   ├── Expense.java
│   │           │   ├── ExpenseShare.java
│   │           │   ├── Budget.java
│   │           │   ├── Category.java
│   │           │   ├── Conversation.java
│   │           │   ├── Message.java
│   │           │   ├── Settlement.java
│   │           │   ├── Subscription.java
│   │           │   ├── SupportTicket.java
│   │           │   └── ...
│   │           │
│   │           ├── repository/         # Repositorios JPA
│   │           │   ├── UserRepository.java
│   │           │   ├── GroupRepository.java
│   │           │   ├── ExpenseRepository.java
│   │           │   └── ...
│   │           │
│   │           ├── service/            # Lógica de negocio
│   │           │   ├── UserService.java
│   │           │   ├── GroupService.java
│   │           │   ├── ExpenseService.java
│   │           │   ├── BudgetService.java
│   │           │   ├── ChatService.java
│   │           │   ├── AIService.java
│   │           │   ├── SubscriptionService.java
│   │           │   ├── SupportService.java
│   │           │   └── ...
│   │           │
│   │           ├── controller/        # Controladores REST
│   │           │   ├── UserController.java
│   │           │   ├── GroupController.java
│   │           │   ├── ExpenseController.java
│   │           │   ├── BudgetController.java
│   │           │   ├── ChatController.java
│   │           │   ├── AIController.java
│   │           │   ├── SubscriptionController.java
│   │           │   ├── SupportController.java
│   │           │   ├── AdminController.java
│   │           │   └── ...
│   │           │
│   │           ├── dto/               # Data Transfer Objects
│   │           │   ├── request/
│   │           │   │   ├── CreateExpenseRequest.java
│   │           │   │   ├── CreateGroupRequest.java
│   │           │   │   └── ...
│   │           │   └── response/
│   │           │       ├── ExpenseResponse.java
│   │           │       ├── GroupResponse.java
│   │           │       └── ...
│   │           │
│   │           ├── mapper/            # Mappers (MapStruct)
│   │           │   ├── ExpenseMapper.java
│   │           │   ├── GroupMapper.java
│   │           │   └── ...
│   │           │
│   │           ├── security/          # Seguridad
│   │           │   ├── SecurityConfig.java
│   │           │   ├── JwtTokenProvider.java
│   │           │   ├── UserDetailsServiceImpl.java
│   │           │   └── ...
│   │           │
│   │           ├── exception/        # Manejo de excepciones
│   │           │   ├── GlobalExceptionHandler.java
│   │           │   ├── ResourceNotFoundException.java
│   │           │   └── ...
│   │           │
│   │           ├── config/           # Configuraciones
│   │           │   ├── DatabaseConfig.java
│   │           │   ├── WebConfig.java
│   │           │   └── ...
│   │           │
│   │           └── util/             # Utilidades
│   │               ├── CurrencyConverter.java
│   │               ├── DateUtils.java
│   │               └── ...
│   │
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       └── db/
│           └── migration/            # Flyway migrations
│               ├── V1__Initial_schema.sql
│               └── ...
│
└── test/
    └── java/
        └── com/
            └── splitia/
                ├── service/
                ├── controller/
                └── ...
```

### 2.2 Capas de la Arquitectura

#### 2.2.1 Capa de Modelo (Model)
- **Responsabilidad**: Representar las entidades del dominio
- **Tecnología**: JPA Entities con Hibernate
- **Ubicación**: `com.splitia.model`
- **Características**:
  - Anotaciones JPA (@Entity, @Table, @Id, etc.)
  - Relaciones entre entidades (@OneToMany, @ManyToOne, etc.)
  - Validaciones (@NotNull, @Size, etc.)
  - Enums para estados y tipos

#### 2.2.2 Capa de Repositorio (Repository)
- **Responsabilidad**: Acceso a datos y consultas a la base de datos
- **Tecnología**: Spring Data JPA
- **Ubicación**: `com.splitia.repository`
- **Características**:
  - Interfaces que extienden JpaRepository
  - Métodos de consulta personalizados
  - Queries nativas cuando sea necesario
  - Paginación y ordenamiento

#### 2.2.3 Capa de Servicio (Service)
- **Responsabilidad**: Lógica de negocio y orquestación
- **Tecnología**: Spring @Service
- **Ubicación**: `com.splitia.service`
- **Características**:
  - Lógica de negocio compleja
  - Transacciones (@Transactional)
  - Validaciones de negocio
  - Integración con servicios externos
  - Manejo de excepciones de negocio

#### 2.2.4 Capa de Controlador (Controller)
- **Responsabilidad**: Manejo de peticiones HTTP y respuestas
- **Tecnología**: Spring @RestController
- **Ubicación**: `com.splitia.controller`
- **Características**:
  - Endpoints RESTful
  - Validación de entrada (@Valid)
  - Manejo de excepciones HTTP
  - Serialización JSON
  - Documentación con Swagger/OpenAPI

---

## 3. Modelo de Datos

### 3.1 Entidades Principales

#### 3.1.1 User (Usuario)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password; // Encriptado
    
    private String phoneNumber;
    private String image;
    private String currency = "PEN";
    private String language = "es";
    private String externalId; // Para auth externa
    
    @OneToMany(mappedBy = "user")
    private List<GroupUser> groupMemberships;
    
    @OneToMany(mappedBy = "paidBy")
    private List<Expense> expenses;
    
    @OneToMany(mappedBy = "user")
    private List<ExpenseShare> expenseShares;
    
    @OneToMany(mappedBy = "user")
    private List<Budget> budgets;
    
    @OneToMany(mappedBy = "user")
    private List<CustomCategory> categories;
    
    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;
    
    // Getters y Setters
}
```

#### 3.1.2 Group (Grupo)
```java
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    private String image;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupUser> members;
    
    @OneToMany(mappedBy = "group")
    private List<Expense> expenses;
    
    @OneToOne(mappedBy = "group")
    private Conversation conversation;
    
    @OneToMany(mappedBy = "group")
    private List<Settlement> settlements;
    
    // Getters y Setters
}
```

#### 3.1.3 GroupUser (Miembro de Grupo)
```java
@Entity
@Table(name = "group_users")
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Enumerated(EnumType.STRING)
    private GroupRole role = GroupRole.MEMBER;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    // Getters y Setters
}

public enum GroupRole {
    ADMIN, MEMBER, GUEST, ASSISTANT
}
```

#### 3.1.4 Expense (Gasto)
```java
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    private String currency = "USD";
    private String location;
    private String notes;
    private Boolean isSettlement = false;
    
    @ManyToOne
    @JoinColumn(name = "paid_by_id", nullable = false)
    private User paidBy;
    
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CustomCategory category;
    
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseShare> shares;
    
    @OneToOne(mappedBy = "expense")
    private Settlement settlement;
    
    // Getters y Setters
}
```

#### 3.1.5 ExpenseShare (Participación en Gasto)
```java
@Entity
@Table(name = "expense_shares")
public class ExpenseShare {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private ShareType type = ShareType.EQUAL;
    
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Getters y Setters
}

public enum ShareType {
    EQUAL, PERCENTAGE, FIXED
}
```

#### 3.1.6 Budget (Presupuesto)
```java
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private Integer month;
    
    @Column(nullable = false)
    private Integer year;
    
    private String currency = "USD";
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CustomCategory category;
    
    // Getters y Setters
}
```

#### 3.1.7 Conversation (Conversación)
```java
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private Boolean isGroupChat = false;
    
    private String name;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<ConversationParticipant> participants;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages;
    
    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;
    
    // Getters y Setters
}
```

#### 3.1.8 Message (Mensaje)
```java
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private Boolean isAI = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageSeen> seenBy;
    
    // Getters y Setters
}
```

#### 3.1.9 Settlement (Asentamiento)
```java
@Entity
@Table(name = "settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private SettlementType type = SettlementType.PAYMENT;
    
    @ManyToOne
    @JoinColumn(name = "initiated_by_id", nullable = false)
    private User initiatedBy;
    
    @ManyToOne
    @JoinColumn(name = "settled_with_user_id", nullable = false)
    private User settledWithUser;
    
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @OneToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    
    // Getters y Setters
}

public enum SettlementStatus {
    PENDING, PENDING_CONFIRMATION, CONFIRMED, COMPLETED, CANCELLED
}

public enum SettlementType {
    PAYMENT, RECEIPT
}
```

#### 3.1.10 Subscription (Suscripción)
```java
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan planType = SubscriptionPlan.FREE;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    private Boolean autoRenew = false;
    private String paymentMethod;
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private BigDecimal pricePerMonth;
    private String currency = "USD";
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<SubscriptionPayment> payments;
    
    // Getters y Setters
}

public enum SubscriptionPlan {
    FREE, PREMIUM, ENTERPRISE
}

public enum SubscriptionStatus {
    ACTIVE, INACTIVE, CANCELLED, EXPIRED, PAST_DUE
}
```

#### 3.1.11 SupportTicket (Ticket de Soporte)
```java
@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private SupportCategory category;
    
    @Enumerated(EnumType.STRING)
    private SupportPriority priority = SupportPriority.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    private SupportStatus status = SupportStatus.OPEN;
    
    private String resolution;
    private LocalDateTime resolvedAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<SupportMessage> messages;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<SupportAttachment> attachments;
    
    // Getters y Setters
}

public enum SupportCategory {
    TECHNICAL, BILLING, FEATURE_REQUEST, BUG_REPORT, ACCOUNT, GENERAL
}

public enum SupportPriority {
    LOW, MEDIUM, HIGH, URGENT
}

public enum SupportStatus {
    OPEN, IN_PROGRESS, PENDING_CUSTOMER, RESOLVED, CLOSED
}
```

### 3.2 Relaciones entre Entidades

- **User ↔ Group**: Many-to-Many a través de GroupUser
- **User ↔ Expense**: One-to-Many (paidBy)
- **Expense ↔ ExpenseShare**: One-to-Many
- **User ↔ ExpenseShare**: One-to-Many
- **Group ↔ Expense**: One-to-Many
- **Group ↔ Conversation**: One-to-One
- **Conversation ↔ Message**: One-to-Many
- **User ↔ Message**: Many-to-One (sender)
- **User ↔ Budget**: One-to-Many
- **User ↔ Subscription**: One-to-Many
- **User ↔ SupportTicket**: One-to-Many (createdBy, assignedTo)

---

## 4. Funcionalidades Detalladas

### 4.1 Gestión de Usuarios

#### 4.1.1 Registro y Autenticación
**Endpoints:**
- `POST /api/auth/register` - Registro de nuevo usuario
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/refresh` - Refrescar token
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/me` - Obtener usuario actual

**Servicios:**
- `UserService.register()` - Validar datos, encriptar contraseña, crear usuario
- `UserService.authenticate()` - Validar credenciales, generar JWT
- `UserService.refreshToken()` - Validar refresh token, generar nuevo JWT

**Validaciones:**
- Email único
- Contraseña mínimo 8 caracteres
- Email válido
- Campos requeridos

#### 4.1.2 Perfil de Usuario
**Endpoints:**
- `GET /api/users/me` - Obtener perfil completo
- `PUT /api/users/me` - Actualizar perfil
- `PUT /api/users/me/password` - Cambiar contraseña
- `PUT /api/users/me/preferences` - Actualizar preferencias

**Servicios:**
- `UserService.getProfile()` - Obtener datos del usuario
- `UserService.updateProfile()` - Actualizar información personal
- `UserService.updatePreferences()` - Actualizar moneda, idioma, tema

### 4.2 Gestión de Grupos

#### 4.2.1 Operaciones CRUD de Grupos
**Endpoints:**
- `GET /api/groups` - Listar grupos del usuario
- `POST /api/groups` - Crear nuevo grupo
- `GET /api/groups/{id}` - Obtener detalles del grupo
- `PUT /api/groups/{id}` - Actualizar grupo
- `DELETE /api/groups/{id}` - Eliminar grupo

**Servicios:**
- `GroupService.createGroup()` - Crear grupo, asignar creador como ADMIN, crear conversación
- `GroupService.getUserGroups()` - Obtener grupos donde el usuario es miembro
- `GroupService.getGroupById()` - Obtener grupo con validación de membresía
- `GroupService.updateGroup()` - Actualizar solo si es ADMIN
- `GroupService.deleteGroup()` - Eliminar solo si es creador

**Reglas de Negocio:**
- Solo ADMIN puede actualizar/eliminar grupo
- Al crear grupo, se crea automáticamente una conversación
- El creador es automáticamente ADMIN

#### 4.2.2 Gestión de Miembros
**Endpoints:**
- `POST /api/groups/{id}/members` - Agregar miembro
- `DELETE /api/groups/{id}/members/{userId}` - Remover miembro
- `PUT /api/groups/{id}/members/{userId}/role` - Cambiar rol
- `POST /api/groups/{id}/invite` - Crear invitación
- `GET /api/groups/invite/{token}` - Verificar token de invitación
- `POST /api/groups/join/{token}` - Unirse a grupo mediante token

**Servicios:**
- `GroupService.addMember()` - Validar permisos ADMIN, agregar miembro
- `GroupService.removeMember()` - Validar permisos, remover miembro
- `GroupService.changeMemberRole()` - Cambiar rol (solo ADMIN)
- `GroupService.createInvitation()` - Generar token único, configurar expiración
- `GroupService.joinByToken()` - Validar token, agregar usuario al grupo

**Reglas de Negocio:**
- Solo ADMIN puede agregar/remover miembros
- No se puede remover al creador del grupo
- Los tokens de invitación pueden tener expiración y límite de usos

#### 4.2.3 Balances de Grupo
**Endpoints:**
- `GET /api/groups/{id}/balances` - Obtener balances del grupo
- `GET /api/groups/{id}/balances/multi-currency` - Balances por moneda

**Servicios:**
- `GroupService.calculateBalances()` - Calcular quién debe a quién
- `GroupService.calculateMultiCurrencyBalances()` - Balances separados por moneda

**Algoritmo de Cálculo:**
1. Obtener todos los gastos del grupo
2. Para cada gasto, calcular las participaciones (shares)
3. Sumar lo que cada usuario pagó vs lo que debe
4. Calcular balance neto por usuario
5. Optimizar asentamientos (minimizar transacciones)

### 4.3 Gestión de Gastos

#### 4.3.1 Operaciones CRUD de Gastos
**Endpoints:**
- `GET /api/expenses` - Listar gastos (con filtros)
- `POST /api/expenses` - Crear gasto
- `GET /api/expenses/{id}` - Obtener detalle de gasto
- `PUT /api/expenses/{id}` - Actualizar gasto
- `DELETE /api/expenses/{id}` - Eliminar gasto

**Servicios:**
- `ExpenseService.createExpense()` - Validar datos, crear gasto y shares
- `ExpenseService.getExpenses()` - Listar con filtros (grupo, categoría, fecha)
- `ExpenseService.getExpenseById()` - Obtener con validación de acceso
- `ExpenseService.updateExpense()` - Actualizar solo si es el pagador o ADMIN del grupo
- `ExpenseService.deleteExpense()` - Eliminar solo si es el pagador o ADMIN

**Tipos de División:**
- **EQUAL**: Dividir igualmente entre todos los miembros
- **PERCENTAGE**: Dividir por porcentajes
- **FIXED**: Montos fijos por persona

**Validaciones:**
- El monto total debe coincidir con la suma de shares
- Solo miembros del grupo pueden crear gastos grupales
- Validar que el pagador sea miembro del grupo (si aplica)

#### 4.3.2 Categorías Personalizadas
**Endpoints:**
- `GET /api/categories` - Listar categorías del usuario
- `POST /api/categories` - Crear categoría
- `PUT /api/categories/{id}` - Actualizar categoría
- `DELETE /api/categories/{id}` - Eliminar categoría

**Servicios:**
- `CategoryService.createCategory()` - Crear categoría personalizada
- `CategoryService.getUserCategories()` - Obtener categorías del usuario
- `CategoryService.updateCategory()` - Actualizar categoría
- `CategoryService.deleteCategory()` - Eliminar si no tiene gastos asociados

### 4.4 Sistema de Presupuestos

#### 4.4.1 Gestión de Presupuestos
**Endpoints:**
- `GET /api/budgets` - Listar presupuestos del usuario
- `POST /api/budgets` - Crear presupuesto
- `GET /api/budgets/{id}` - Obtener presupuesto
- `PUT /api/budgets/{id}` - Actualizar presupuesto
- `DELETE /api/budgets/{id}` - Eliminar presupuesto
- `GET /api/budgets/monthly/{month}/{year}` - Presupuesto mensual

**Servicios:**
- `BudgetService.createBudget()` - Crear presupuesto para mes/año/categoría
- `BudgetService.getMonthlyBudget()` - Obtener presupuesto del mes
- `BudgetService.calculateSpent()` - Calcular gastado vs presupuestado
- `BudgetService.updateBudget()` - Actualizar monto del presupuesto

**Cálculo de Progreso:**
1. Obtener presupuesto del mes
2. Sumar gastos del mes por categoría
3. Calcular porcentaje gastado
4. Generar alertas si excede presupuesto

### 4.5 Sistema de Chat

#### 4.5.1 Conversaciones
**Endpoints:**
- `GET /api/conversations` - Listar conversaciones del usuario
- `POST /api/conversations` - Crear conversación individual
- `POST /api/conversations/group` - Crear chat de grupo
- `GET /api/conversations/{id}` - Obtener conversación
- `DELETE /api/conversations/{id}` - Eliminar conversación

**Servicios:**
- `ChatService.createConversation()` - Crear conversación entre usuarios
- `ChatService.createGroupChat()` - Crear chat para grupo existente
- `ChatService.getUserConversations()` - Obtener conversaciones del usuario
- `ChatService.getConversationById()` - Obtener con validación de participación

#### 4.5.2 Mensajes
**Endpoints:**
- `GET /api/conversations/{id}/messages` - Listar mensajes
- `POST /api/conversations/{id}/messages` - Enviar mensaje
- `PUT /api/messages/{id}/read` - Marcar como leído
- `DELETE /api/messages/{id}` - Eliminar mensaje

**Servicios:**
- `ChatService.sendMessage()` - Crear mensaje, notificar participantes
- `ChatService.getMessages()` - Obtener mensajes con paginación
- `ChatService.markAsRead()` - Marcar mensaje como leído
- `ChatService.deleteMessage()` - Eliminar mensaje (solo propio)

**Características:**
- Mensajes en tiempo real (WebSocket)
- Indicadores de lectura
- Historial completo
- Búsqueda de mensajes

### 4.6 Asistente de IA

#### 4.6.1 Procesamiento de Mensajes
**Endpoints:**
- `POST /api/ai/process-message` - Procesar mensaje y ejecutar acción
- `GET /api/ai/actions` - Historial de acciones de IA
- `POST /api/ai/query` - Consulta directa al asistente

**Servicios:**
- `AIService.processMessage()` - Analizar mensaje, determinar intención
- `AIService.executeAction()` - Ejecutar acción (crear gasto, grupo, etc.)
- `AIService.getActionHistory()` - Obtener historial de acciones

**Acciones Soportadas:**
- CREATE_EXPENSE - Crear gasto desde texto natural
- UPDATE_EXPENSE - Actualizar gasto existente
- DELETE_EXPENSE - Eliminar gasto
- CREATE_GROUP - Crear grupo
- ADD_USER_TO_GROUP - Agregar usuario a grupo
- SUGGEST_SETTLEMENT - Sugerir asentamiento
- CREATE_GROUP_CHAT - Crear chat de grupo
- SEND_MESSAGE - Enviar mensaje

**Procesamiento:**
1. Analizar texto con NLP
2. Extraer entidades (monto, fecha, personas, etc.)
3. Determinar acción a ejecutar
4. Validar permisos
5. Ejecutar acción
6. Registrar en historial

### 4.7 Sistema de Asentamientos (Settlements)

#### 4.7.1 Gestión de Asentamientos
**Endpoints:**
- `GET /api/groups/{id}/settlements` - Listar asentamientos
- `POST /api/groups/{id}/settlements` - Crear asentamiento
- `PUT /api/settlements/{id}/status` - Actualizar estado
- `POST /api/settlements/{id}/confirm` - Confirmar asentamiento

**Servicios:**
- `SettlementService.createSettlement()` - Crear registro de pago
- `SettlementService.getGroupSettlements()` - Listar asentamientos del grupo
- `SettlementService.updateStatus()` - Actualizar estado (PENDING → CONFIRMED → COMPLETED)
- `SettlementService.confirmSettlement()` - Confirmar recepción de pago

**Flujo de Asentamiento:**
1. Usuario A debe X a Usuario B
2. Usuario A crea asentamiento (PAYMENT)
3. Usuario B confirma recepción
4. Estado cambia a COMPLETED
5. Se crea gasto de asentamiento asociado

### 4.8 Sistema de Suscripciones

#### 4.8.1 Gestión de Planes
**Endpoints:**
- `GET /api/subscriptions/plans` - Listar planes disponibles
- `GET /api/subscriptions/current` - Obtener suscripción actual
- `POST /api/subscriptions/checkout` - Crear sesión de checkout
- `POST /api/subscriptions/webhook` - Webhook de Stripe
- `PUT /api/subscriptions/cancel` - Cancelar suscripción

**Servicios:**
- `SubscriptionService.getAvailablePlans()` - Listar planes
- `SubscriptionService.getCurrentSubscription()` - Obtener suscripción del usuario
- `SubscriptionService.createCheckoutSession()` - Crear sesión de Stripe
- `SubscriptionService.handleWebhook()` - Procesar eventos de Stripe
- `SubscriptionService.cancelSubscription()` - Cancelar suscripción

**Planes:**
- **FREE**: Funcionalidades básicas
- **PREMIUM**: Todas las funcionalidades + IA
- **ENTERPRISE**: Premium + características empresariales

**Integración Stripe:**
- Crear customer en Stripe
- Crear subscription
- Procesar webhooks (payment_succeeded, payment_failed, etc.)
- Actualizar estado de suscripción

### 4.9 Sistema de Soporte

#### 4.9.1 Tickets de Soporte
**Endpoints:**
- `GET /api/support/tickets` - Listar tickets del usuario
- `POST /api/support/tickets` - Crear ticket
- `GET /api/support/tickets/{id}` - Obtener ticket
- `PUT /api/support/tickets/{id}` - Actualizar ticket
- `POST /api/support/tickets/{id}/messages` - Agregar mensaje
- `PUT /api/support/tickets/{id}/status` - Cambiar estado

**Servicios:**
- `SupportService.createTicket()` - Crear ticket con categoría y prioridad
- `SupportService.getUserTickets()` - Listar tickets del usuario
- `SupportService.getTicketById()` - Obtener ticket con mensajes
- `SupportService.addMessage()` - Agregar mensaje al ticket
- `SupportService.updateStatus()` - Actualizar estado del ticket
- `SupportService.assignTicket()` - Asignar ticket a agente (admin)

**Flujo de Soporte:**
1. Usuario crea ticket
2. Sistema asigna automáticamente a agente disponible
3. Agente responde
4. Usuario puede agregar más información
5. Ticket se resuelve y cierra

### 4.10 Panel Administrativo

#### 4.10.1 Gestión de Usuarios (Admin)
**Endpoints:**
- `GET /api/admin/users` - Listar todos los usuarios (con filtros)
- `GET /api/admin/users/{id}` - Obtener usuario detallado
- `PUT /api/admin/users/{id}/status` - Cambiar estado (suspend/activate)
- `DELETE /api/admin/users/{id}` - Eliminar usuario
- `PUT /api/admin/users/{id}/subscription` - Cambiar plan

**Servicios:**
- `AdminService.getAllUsers()` - Listar con paginación y filtros
- `AdminService.getUserDetails()` - Obtener información completa
- `AdminService.suspendUser()` - Suspender cuenta
- `AdminService.activateUser()` - Activar cuenta
- `AdminService.changeUserPlan()` - Cambiar plan de suscripción

#### 4.10.2 Gestión de Grupos (Admin)
**Endpoints:**
- `GET /api/admin/groups` - Listar todos los grupos
- `GET /api/admin/groups/{id}` - Obtener grupo detallado
- `PUT /api/admin/groups/{id}/suspend` - Suspender grupo
- `DELETE /api/admin/groups/{id}` - Eliminar grupo

**Servicios:**
- `AdminService.getAllGroups()` - Listar grupos con estadísticas
- `AdminService.getGroupDetails()` - Obtener grupo con toda la información
- `AdminService.suspendGroup()` - Suspender grupo problemático
- `AdminService.deleteGroup()` - Eliminar grupo

#### 4.10.3 Reportes y Métricas (Admin)
**Endpoints:**
- `GET /api/admin/metrics/overview` - Métricas generales
- `GET /api/admin/metrics/users` - Métricas de usuarios
- `GET /api/admin/metrics/revenue` - Métricas de ingresos
- `GET /api/admin/reports/custom` - Reportes personalizados

**Servicios:**
- `AdminService.getOverviewMetrics()` - Total usuarios, grupos, ingresos
- `AdminService.getUserMetrics()` - Crecimiento, retención, engagement
- `AdminService.getRevenueMetrics()` - MRR, ARR, churn rate
- `AdminService.generateCustomReport()` - Generar reporte personalizado

---

## 5. Seguridad y Autenticación

### 5.1 Autenticación JWT
- **Token de Acceso**: Válido por 15 minutos
- **Refresh Token**: Válido por 7 días
- **Algoritmo**: HS256
- **Claims**: userId, email, roles

### 5.2 Autorización
- **Roles de Usuario**: USER, ADMIN, SUPPORT_AGENT, DEVELOPER
- **Roles de Grupo**: ADMIN, MEMBER, GUEST, ASSISTANT
- **Spring Security**: Configuración de rutas protegidas
- **Method Security**: @PreAuthorize para métodos

### 5.3 Validaciones
- **Input Validation**: Bean Validation (@Valid)
- **SQL Injection**: Uso de parámetros en queries
- **XSS Protection**: Sanitización de inputs
- **CSRF Protection**: Tokens CSRF para formularios

### 5.4 Encriptación
- **Contraseñas**: BCrypt con salt
- **Datos Sensibles**: Encriptación AES-256
- **Comunicación**: HTTPS obligatorio

---

## 6. API RESTful

### 6.1 Convenciones
- **URLs**: `/api/{resource}/{id}`
- **Métodos HTTP**: GET, POST, PUT, DELETE, PATCH
- **Formato**: JSON
- **Códigos HTTP**: 200, 201, 400, 401, 403, 404, 500

### 6.2 Paginación
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

### 6.3 Respuestas Estándar
**Éxito:**
```json
{
  "success": true,
  "data": {...},
  "message": "Operation successful"
}
```

**Error:**
```json
{
  "success": false,
  "error": "Error code",
  "message": "Error message",
  "details": {...}
}
```

### 6.4 Documentación API
- **Swagger/OpenAPI**: Documentación interactiva
- **Endpoints**: Todos documentados
- **Ejemplos**: Request/Response examples

---

## 7. Testing

### 7.1 Unit Tests
- **Services**: Lógica de negocio
- **Repositories**: Consultas a base de datos
- **Utils**: Utilidades y helpers

### 7.2 Integration Tests
- **Controllers**: Endpoints REST
- **Services**: Integración entre servicios
- **Database**: Tests con base de datos en memoria (H2)

### 7.3 Test Coverage
- **Objetivo**: Mínimo 80% de cobertura
- **Herramientas**: JaCoCo
- **CI/CD**: Ejecutar tests en pipeline

---

## 8. Despliegue y Configuración

### 8.1 Configuración
- **application.properties**: Configuración base
- **application-dev.properties**: Desarrollo
- **application-prod.properties**: Producción

### 8.2 Variables de Entorno
- `DATABASE_URL`: URL de PostgreSQL
- `JWT_SECRET`: Secreto para JWT
- `STRIPE_SECRET_KEY`: Clave de Stripe
- `AI_API_KEY`: Clave de API de IA

### 8.3 Base de Datos
- **Migraciones**: Flyway
- **Scripts**: En `resources/db/migration/`
- **Versionado**: V1__, V2__, etc.

### 8.4 Build y Deploy
- **Build**: Maven/Gradle
- **Docker**: Containerización
- **CI/CD**: GitHub Actions / Jenkins

---

## 9. Monitoreo y Logging

### 9.1 Logging
- **Framework**: Logback / SLF4J
- **Niveles**: DEBUG, INFO, WARN, ERROR
- **Formato**: JSON para producción
- **Rotación**: Por tamaño y fecha

### 9.2 Métricas
- **Actuator**: Endpoints de métricas
- **Health Checks**: `/actuator/health`
- **Prometheus**: Exportación de métricas

### 9.3 Alertas
- **Errores Críticos**: Notificaciones inmediatas
- **Performance**: Alertas de latencia
- **Disponibilidad**: Monitoreo de uptime

---

## 10. Consideraciones de Performance

### 10.1 Optimizaciones
- **Caching**: Redis para datos frecuentes
- **Lazy Loading**: Carga diferida de relaciones
- **Paginación**: Todas las listas paginadas
- **Índices**: Índices en campos de búsqueda frecuente

### 10.2 Escalabilidad
- **Horizontal**: Múltiples instancias
- **Load Balancing**: Distribución de carga
- **Database**: Read replicas para consultas

### 10.3 Límites
- **Rate Limiting**: Por usuario/IP
- **Tamaño de Request**: Máximo 10MB
- **Timeout**: 30 segundos por request

---

## 11. Roadmap de Implementación

### Fase 1: Core (Semanas 1-4)
- Configuración del proyecto
- Modelos de datos básicos
- Autenticación y autorización
- CRUD de usuarios y grupos
- CRUD de gastos básicos

### Fase 2: Funcionalidades Principales (Semanas 5-8)
- Sistema de balances y asentamientos
- Sistema de presupuestos
- Sistema de chat básico
- Categorías personalizadas

### Fase 3: Funcionalidades Avanzadas (Semanas 9-12)
- Asistente de IA
- Sistema de suscripciones
- Sistema de soporte
- Panel administrativo básico

### Fase 4: Optimización y Testing (Semanas 13-16)
- Optimizaciones de performance
- Testing completo
- Documentación
- Preparación para producción

---

## 12. Apéndices

### 12.1 Diagramas
- Diagrama de arquitectura
- Diagrama de base de datos
- Diagrama de flujo de autenticación
- Diagrama de flujo de creación de gasto

### 12.2 Glosario
- **Gasto**: Transacción financiera registrada
- **Share**: Participación de un usuario en un gasto
- **Settlement**: Asentamiento de deuda entre usuarios
- **Group**: Grupo de usuarios que comparten gastos
- **Budget**: Presupuesto mensual por categoría

### 12.3 Referencias
- Documentación de Spring Boot
- Documentación de JPA/Hibernate
- Documentación de PostgreSQL
- Documentación de Stripe API

---

**Versión del Documento**: 1.0  
**Fecha**: 2024  
**Autor**: Equipo de Desarrollo Splitia

