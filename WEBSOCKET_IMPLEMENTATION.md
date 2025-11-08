# WebSocket Implementation - Splitia CRM/ERP

## Resumen de Implementación

Se ha implementado WebSocket completo en toda la aplicación usando Spring WebSocket con STOMP.

## Componentes Implementados

### 1. Configuración
- **WebSocketSecurityConfig.java**: Configuración de WebSocket con STOMP
  - Endpoint: `/ws`
  - Topics: `/topic/*`, `/queue/*`, `/user/*`
  - Application prefix: `/app`

### 2. Servicio de Notificaciones
- **WebSocketNotificationService.java**: Servicio centralizado para emitir eventos WebSocket
  - Métodos para todos los módulos principales
  - Soporte para topics, queues y mensajes privados

### 3. DTOs
- **WebSocketMessage.java**: DTO estándar para mensajes WebSocket
  - Campos: type, module, action, entityId, entityType, data, userId, timestamp

### 4. Integración en Servicios
- **OpportunityService**: Notificaciones para creación, actualización y cambio de etapa
- **LeadService**: Notificaciones para creación y conversión a oportunidad
- Más servicios se pueden integrar siguiendo el mismo patrón

## Canales WebSocket Disponibles

### CRM - Sales
- `/topic/sales/opportunities` - Eventos de oportunidades
- `/topic/sales/leads` - Eventos de leads
- `/topic/sales/activities` - Eventos de actividades
- `/topic/sales/pipeline` - Cambios en el pipeline

### CRM - Contacts
- `/topic/crm/contacts` - Eventos de contactos
- `/topic/crm/companies` - Eventos de empresas

### Support
- `/topic/support/tickets` - Eventos de tickets de soporte

### Finance
- `/topic/finance/invoices` - Eventos de facturas
- `/topic/finance/payments` - Eventos de pagos

### Projects
- `/topic/projects` - Eventos generales de proyectos
- `/topic/projects/{id}` - Eventos específicos de un proyecto
- `/topic/projects/{id}/tasks` - Eventos de tareas de un proyecto
- `/topic/tasks` - Eventos generales de tareas

### System
- `/topic/notifications` - Notificaciones globales
- `/topic/sla/alerts` - Alertas SLA

## Tipos de Eventos

### Sales
- `OPPORTUNITY_CREATED`
- `OPPORTUNITY_UPDATED`
- `OPPORTUNITY_STAGE_CHANGED`
- `LEAD_CREATED`
- `LEAD_CONVERTED`
- `ACTIVITY_CREATED`

### Contacts
- `CONTACT_CREATED`
- `CONTACT_UPDATED`
- `COMPANY_CREATED`

### Support
- `TICKET_CREATED`
- `TICKET_UPDATED`
- `TICKET_STATUS_CHANGED`

### Finance
- `INVOICE_CREATED`
- `INVOICE_PAID`
- `PAYMENT_RECEIVED`

### System
- `GLOBAL_NOTIFICATION`
- `SLA_ALERT`

## Uso en Frontend

```typescript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = new Client({
  webSocketFactory: () => socket,
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});

stompClient.onConnect = () => {
  // Suscribirse a eventos de oportunidades
  stompClient.subscribe('/topic/sales/opportunities', (message) => {
    const event = JSON.parse(message.body);
    console.log('Nueva oportunidad:', event);
    // Actualizar UI
  });
  
  // Suscribirse a notificaciones globales
  stompClient.subscribe('/topic/notifications', (message) => {
    const notification = JSON.parse(message.body);
    showNotification(notification);
  });
};

stompClient.activate();
```

## Próximos Pasos

1. Integrar WebSocket en los servicios restantes:
   - ContactService
   - CompanyService
   - ActivityService
   - SupportService
   - FinanceService (cuando se implemente)
   - ProjectService (cuando se implemente)

2. Agregar autenticación WebSocket más robusta si es necesario

3. Implementar rate limiting para WebSocket si es necesario

4. Agregar métricas y monitoreo de conexiones WebSocket

