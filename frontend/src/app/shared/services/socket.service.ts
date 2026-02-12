import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';
import { AuthService } from '../../core/auth/services/auth.service';

@Injectable({ providedIn: 'root' })
export class SocketService {
  private stompClient: Client | null = null;
  public notificationSubject = new BehaviorSubject<NotificationDTO | null>(null);

    constructor(private authService: AuthService) {
    setTimeout(() => {
      if (this.authService.isLoggedIn()) {
        this.initializeWebSocketConnection();
      }
    });
  }

  initializeWebSocketConnection() {
    if (this.stompClient?.active) return; 

    const userId = this.authService.getUserId();
    if (!userId) return;

    const socket = new SockJS('http://localhost:8080/ws-transport');
    
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: (frame) => {
        console.log('Connected to Notifications WebSocket');
        this.stompClient?.subscribe(`/topic/notifications/${userId}`, (message) => {
          this.notificationSubject.next(JSON.parse(message.body));
        });
      }
    });
    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
      console.log("disconnected from notifications");
    }
  }
}