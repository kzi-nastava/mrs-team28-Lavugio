package com.backend.lavugio.service.notification;

import com.backend.lavugio.dto.ride.PanicNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PanicNotificationWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send panic notification to all admins via WebSocket
     * Admins subscribe to /socket-publisher/admin/panic
     */
    public void broadcastPanicAlert(PanicNotificationDTO panicAlert) {
        messagingTemplate.convertAndSend(
                "/socket-publisher/admin/panic",
                panicAlert
        );
    }

    /**
     * Send panic notification to specific admin
     */
    public void sendPanicAlertToAdmin(Long adminId, PanicNotificationDTO panicAlert) {
        messagingTemplate.convertAndSendToUser(
                adminId.toString(),
                "/panic",
                panicAlert
        );
    }

    /**
     * Broadcast panic status update to all connected clients
     */
    public void broadcastPanicStatusUpdate(Long rideId, String status) {
        messagingTemplate.convertAndSend(
                "/socket-publisher/ride/" + rideId + "/panic-status",
                status
        );
    }
}
