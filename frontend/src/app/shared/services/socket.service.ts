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
  private isConnected = false;

    constructor(private authService: AuthService) {}

  initializeWebSocketConnection() {
    if (this.stompClient?.active) {
      console.log('WebSocket already connected');
      return;
    }
    const token = this.authService.getToken();
    const userId = this.authService.getUserId();
    if (!token || !userId) {
      console.log('No token or userId - WebSocket not connected');
      return;
    }
    console.log('Initializing WebSocket with token and userId:', userId);
    const headers = {
      Authorization: `Bearer ${token}`
    };

    const socket = new SockJS('http://localhost:8080/ws-transport');
    
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      connectHeaders: headers,
      reconnectDelay: 5000,
      onConnect: (frame) => {
        console.log('Connected to Notifications WebSocket');
        this.isConnected = true;
        
        // Subscribe to notifications
        this.stompClient?.subscribe(`/topic/notifications/${userId}`, (message) => {
          console.log('Notification received:', message.body);
          this.notificationSubject.next(JSON.parse(message.body));
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
        this.isConnected = false;
      },
      onWebSocketError: (event) => {
        console.error('WebSocket error', event);
        this.isConnected = false;
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
        this.isConnected = false;
      }
    });
    
    this.stompClient.activate();
  }

  connect() {
    this.initializeWebSocketConnection();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
      this.isConnected = false;
      console.log("disconnected from notifications");
    }
  }

  sendMessage(destination: string, body: any) {
        if (!this.stompClient?.active) {
      console.warn('WebSocket not connected - attempting to reconnect...');
      this.connect();
      // Sačekaj malo pa pokušaj ponovo
      setTimeout(() => {
        if (this.stompClient?.active) {
          this.stompClient.publish({
            destination: destination,
            body: JSON.stringify(body)
          });
        } else {
          console.error('Still not connected after retry!');
        }
      }, 1000);
      return;
    }
    this.stompClient.publish({
      destination: destination,
      body: JSON.stringify(body)
    });
  }

  subscribeToTopic(topic: string, callback: (payload: any) => void) {
    const attemptSubscribe = () => {
      if (this.stompClient && this.stompClient.connected) {
        return this.stompClient.subscribe(topic, (message: Message) => {
          callback(JSON.parse(message.body));
        });
      } else {
        setTimeout(() => attemptSubscribe(), 500);
        return null;
      }
    };

    return attemptSubscribe();
  }

  isWebSocketConnected(): boolean {
    return this.stompClient?.active || false;
  }
}