package com.splitia.service.websocket;

import com.splitia.dto.websocket.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send notification to all users subscribed to a topic
     */
    public void sendToTopic(String topic, WebSocketMessage message) {
        messagingTemplate.convertAndSend(topic, message);
        log.debug("Sent WebSocket message to topic {}: {}", topic, message.getType());
    }
    
    /**
     * Send notification to a specific user
     */
    public void sendToUser(String username, String destination, WebSocketMessage message) {
        messagingTemplate.convertAndSendToUser(username, destination, message);
        log.debug("Sent WebSocket message to user {}: {}", username, message.getType());
    }
    
    /**
     * Send notification to all users subscribed to a queue
     */
    public void sendToQueue(String queue, WebSocketMessage message) {
        messagingTemplate.convertAndSend("/queue" + queue, message);
        log.debug("Sent WebSocket message to queue {}: {}", queue, message.getType());
    }
    
    // CRM - Sales Events
    public void notifyOpportunityCreated(UUID opportunityId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "OPPORTUNITY_CREATED", "CRM", "CREATED", opportunityId, "Opportunity"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/sales/opportunities", message);
    }
    
    public void notifyOpportunityUpdated(UUID opportunityId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "OPPORTUNITY_UPDATED", "CRM", "UPDATED", opportunityId, "Opportunity"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/sales/opportunities", message);
    }
    
    public void notifyOpportunityStageChanged(UUID opportunityId, String oldStage, String newStage, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "OPPORTUNITY_STAGE_CHANGED", "CRM", "STATUS_CHANGED", opportunityId, "Opportunity"
        );
        message.setData(Map.of("oldStage", oldStage, "newStage", newStage));
        message.setUserId(userId);
        sendToTopic("/topic/sales/opportunities", message);
        sendToTopic("/topic/sales/pipeline", message);
    }
    
    public void notifyLeadCreated(UUID leadId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "LEAD_CREATED", "CRM", "CREATED", leadId, "Lead"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/sales/leads", message);
    }
    
    public void notifyLeadConverted(UUID leadId, UUID opportunityId, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "LEAD_CONVERTED", "CRM", "CONVERTED", leadId, "Lead"
        );
        message.setData(Map.of("opportunityId", opportunityId.toString()));
        message.setUserId(userId);
        sendToTopic("/topic/sales/leads", message);
        sendToTopic("/topic/sales/opportunities", message);
    }
    
    public void notifyActivityCreated(UUID activityId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "ACTIVITY_CREATED", "CRM", "CREATED", activityId, "Activity"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/sales/activities", message);
    }
    
    // CRM - Contacts Events
    public void notifyContactCreated(UUID contactId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "CONTACT_CREATED", "CRM", "CREATED", contactId, "Contact"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/crm/contacts", message);
    }
    
    public void notifyContactUpdated(UUID contactId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "CONTACT_UPDATED", "CRM", "UPDATED", contactId, "Contact"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/crm/contacts", message);
    }
    
    public void notifyCompanyCreated(UUID companyId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "COMPANY_CREATED", "CRM", "CREATED", companyId, "Company"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/crm/companies", message);
    }
    
    // Support Events
    public void notifyTicketCreated(UUID ticketId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "TICKET_CREATED", "SUPPORT", "CREATED", ticketId, "SupportTicket"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/support/tickets", message);
    }
    
    public void notifyTicketUpdated(UUID ticketId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "TICKET_UPDATED", "SUPPORT", "UPDATED", ticketId, "SupportTicket"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/support/tickets", message);
    }
    
    public void notifyTicketStatusChanged(UUID ticketId, String oldStatus, String newStatus, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "TICKET_STATUS_CHANGED", "SUPPORT", "STATUS_CHANGED", ticketId, "SupportTicket"
        );
        message.setData(Map.of("oldStatus", oldStatus, "newStatus", newStatus));
        message.setUserId(userId);
        sendToTopic("/topic/support/tickets", message);
    }
    
    // Finance Events
    public void notifyInvoiceCreated(UUID invoiceId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "INVOICE_CREATED", "FINANCE", "CREATED", invoiceId, "Invoice"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/finance/invoices", message);
    }
    
    public void notifyInvoiceUpdated(UUID invoiceId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "INVOICE_UPDATED", "FINANCE", "UPDATED", invoiceId, "Invoice"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/finance/invoices", message);
    }
    
    public void notifyInvoicePaid(UUID invoiceId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "INVOICE_PAID", "FINANCE", "PAID", invoiceId, "Invoice"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/finance/invoices", message);
        sendToTopic("/topic/finance/payments", message);
    }
    
    public void notifyPaymentReceived(UUID paymentId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PAYMENT_RECEIVED", "FINANCE", "CREATED", paymentId, "Payment"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/finance/payments", message);
    }
    
    // General Notifications
    public void notifyGlobalNotification(String title, String message, String level, UUID userId) {
        WebSocketMessage wsMessage = new WebSocketMessage(
            "GLOBAL_NOTIFICATION", "SYSTEM", "NOTIFICATION", null, "Notification"
        );
        wsMessage.setData(Map.of(
            "title", title,
            "message", message,
            "level", level
        ));
        wsMessage.setUserId(userId);
        sendToTopic("/topic/notifications", wsMessage);
    }
    
    public void notifySlaAlert(String alertType, Map<String, Object> data) {
        WebSocketMessage message = new WebSocketMessage(
            "SLA_ALERT", "SYSTEM", "ALERT", null, "SlaAlert"
        );
        message.setData(data);
        sendToTopic("/topic/sla/alerts", message);
    }
    
    // Project Events
    public void notifyProjectUpdated(UUID projectId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PROJECT_UPDATED", "PROJECTS", "UPDATED", projectId, "Project"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/projects/" + projectId, message);
        sendToTopic("/topic/projects", message);
    }
    
    public void notifyTaskUpdated(UUID taskId, UUID projectId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "TASK_UPDATED", "PROJECTS", "UPDATED", taskId, "Task"
        );
        message.setData(data);
        message.setUserId(userId);
        if (projectId != null) {
            sendToTopic("/topic/projects/" + projectId + "/tasks", message);
        }
        sendToTopic("/topic/tasks", message);
    }
    
    // Inventory Events
    public void notifyProductCreated(UUID productId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PRODUCT_CREATED", "INVENTORY", "CREATED", productId, "Product"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/inventory/products", message);
    }
    
    public void notifyProductUpdated(UUID productId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PRODUCT_UPDATED", "INVENTORY", "UPDATED", productId, "Product"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/inventory/products", message);
    }
    
    public void notifyStockLow(UUID productId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "STOCK_LOW", "INVENTORY", "ALERT", productId, "Stock"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/inventory/alerts", message);
        sendToTopic("/topic/notifications", message);
    }
    
    public void notifyStockMovement(UUID movementId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "STOCK_MOVEMENT", "INVENTORY", "MOVEMENT", movementId, "StockMovement"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/inventory/movements", message);
    }
    
    // Procurement Events
    public void notifyVendorCreated(UUID vendorId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "VENDOR_CREATED", "PROCUREMENT", "CREATED", vendorId, "Vendor"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/procurement/vendors", message);
    }
    
    public void notifyPurchaseOrderCreated(UUID purchaseOrderId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PURCHASE_ORDER_CREATED", "PROCUREMENT", "CREATED", purchaseOrderId, "PurchaseOrder"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/procurement/purchase-orders", message);
    }
    
    public void notifyPurchaseOrderStatusChanged(UUID purchaseOrderId, String oldStatus, String newStatus, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PURCHASE_ORDER_STATUS_CHANGED", "PROCUREMENT", "STATUS_CHANGED", purchaseOrderId, "PurchaseOrder"
        );
        message.setData(Map.of("oldStatus", oldStatus, "newStatus", newStatus));
        message.setUserId(userId);
        sendToTopic("/topic/procurement/purchase-orders", message);
    }
    
    // Marketing Events
    public void notifyCampaignStatusChanged(UUID campaignId, String oldStatus, String newStatus, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "CAMPAIGN_STATUS_CHANGED", "MARKETING", "STATUS_CHANGED", campaignId, "Campaign"
        );
        message.setData(Map.of("oldStatus", oldStatus, "newStatus", newStatus));
        message.setUserId(userId);
        sendToTopic("/topic/marketing/campaigns", message);
    }
    
    public void notifyEmailSent(UUID campaignContactId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "EMAIL_SENT", "MARKETING", "SENT", campaignContactId, "CampaignContact"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/marketing/campaigns", message);
    }
    
    // HR Events
    public void notifyEmployeeCreated(UUID employeeId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "EMPLOYEE_CREATED", "HR", "CREATED", employeeId, "Employee"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/hr/employees", message);
    }
    
    public void notifyAttendanceCreated(UUID attendanceId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "ATTENDANCE_CREATED", "HR", "CREATED", attendanceId, "Attendance"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/hr/attendance", message);
    }
    
    public void notifyAttendanceAlert(String alertType, Map<String, Object> data) {
        WebSocketMessage message = new WebSocketMessage(
            "ATTENDANCE_ALERT", "HR", "ALERT", null, "Attendance"
        );
        message.setData(data);
        sendToTopic("/topic/hr/alerts", message);
        sendToTopic("/topic/notifications", message);
    }
    
    public void notifyPayrollGenerated(UUID payrollId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PAYROLL_GENERATED", "HR", "GENERATED", payrollId, "Payroll"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/hr/payroll", message);
    }
    
    // Project Events
    public void notifyProjectStatusChanged(UUID projectId, String oldStatus, String newStatus, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "PROJECT_STATUS_CHANGED", "PROJECTS", "STATUS_CHANGED", projectId, "Project"
        );
        message.setData(Map.of("oldStatus", oldStatus, "newStatus", newStatus));
        message.setUserId(userId);
        sendToTopic("/topic/projects/" + projectId, message);
        sendToTopic("/topic/projects", message);
    }
    
    public void notifyTimeEntryCreated(UUID timeEntryId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "TIME_ENTRY_CREATED", "PROJECTS", "CREATED", timeEntryId, "TimeEntry"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/projects/time-entries", message);
    }
    
    // Transversal Events
    public void notifyWorkflowExecuted(UUID workflowId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "WORKFLOW_EXECUTED", "WORKFLOW", "EXECUTED", workflowId, "Workflow"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/workflows", message);
    }
    
    public void notifyAuditLogCreated(UUID auditLogId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "AUDIT_LOG_CREATED", "AUDIT", "CREATED", auditLogId, "AuditLog"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/audit/logs", message);
    }
    
    // Chat Events
    public void notifyMessageCreated(UUID messageId, UUID conversationId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "MESSAGE_CREATED", "CHAT", "CREATED", messageId, "Message"
        );
        message.setData(data);
        message.setUserId(userId);
        // Send to specific conversation topic
        sendToTopic("/topic/chat/conversations/" + conversationId + "/messages", message);
    }
    
    public void notifyMessageUpdated(UUID messageId, UUID conversationId, Map<String, Object> data, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "MESSAGE_UPDATED", "CHAT", "UPDATED", messageId, "Message"
        );
        message.setData(data);
        message.setUserId(userId);
        sendToTopic("/topic/chat/conversations/" + conversationId + "/messages", message);
    }
    
    public void notifyMessageDeleted(UUID messageId, UUID conversationId, UUID userId) {
        WebSocketMessage message = new WebSocketMessage(
            "MESSAGE_DELETED", "CHAT", "DELETED", messageId, "Message"
        );
        message.setData(Map.of("messageId", messageId.toString(), "conversationId", conversationId.toString()));
        message.setUserId(userId);
        sendToTopic("/topic/chat/conversations/" + conversationId + "/messages", message);
    }
}

