import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { SocketService } from '../../shared/services/socket.service';
import { AuthService } from '../../core/auth/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-support-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './support-chat.component.html',
  styleUrl: './support-chat.component.css',
})
export class SupportChatComponent {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;
    isOpen = false;
    adminView: 'user-list' | 'chat' = 'user-list'; // for admin only
    chatUsers: any[] = []; // list of users for admin
    messages: any[] = [];
    newMessage = '';
    currentUserId: number | null = null;
    userRole: string | null = null;
    selectedChatUserId: number | null = null;
    private chatSubscription?: any;
    private authSubscription?: any;

  constructor(
    private http: HttpClient,
    private socketService: SocketService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef

  ) {}

  ngOnInit(): void {
  this.authSubscription = this.authService.getAuthState().subscribe((isLoggedIn) => {
    console.log('Auth state changed, isLoggedIn:', isLoggedIn);
    
    if (isLoggedIn) {
      setTimeout(() => {
        this.socketService.connect();
      }, 100);
      
      this.initializeComponent();
    } else {
      this.resetComponent();
      this.socketService.disconnect();
    }
  });
    if (this.authService.isLoggedIn()) {
    this.socketService.connect();
    this.initializeComponent();
  }
}

initializeComponent(): void {
  this.currentUserId = this.authService.getUserId();
  this.userRole = this.authService.getRole();
  console.log('User Role:', this.userRole);

  if (this.currentUserId) {
    setTimeout(() => {
      this.subscribeToMessages();
      
      if (this.userRole === 'ROLE_ADMIN') {
        this.adminView = 'user-list';
        this.loadChatUsers();
      } else {
        this.adminView = 'chat';
        if (this.currentUserId) {
          this.loadHistory(this.currentUserId);
        }
      }
    }, 500);
  }
}

resetComponent(): void {
  this.currentUserId = null;
  this.userRole = null;
  this.messages = [];
  this.selectedChatUserId = null;
  this.chatUsers = [];
  if (this.chatSubscription) {
    this.chatSubscription.unsubscribe();
  }
}

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
    if (this.isOpen && this.userRole === 'ROLE_ADMIN') {
      this.loadChatUsers(); // refresh list when opening
    }
    if (this.isOpen) {
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  loadChatUsers() {
    this.http.get<any[]>('http://localhost:8080/api/v1/chat/users').subscribe({
      next: (users) => {
        this.chatUsers = users;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load chat users', err)
    });
  }

  selectUser(userId: number) {
    this.selectedChatUserId = userId;
    this.adminView = 'chat';
    this.loadHistory(userId);
  }

  backToUserList() {
    this.adminView = 'user-list';
    this.selectedChatUserId = null;
    this.messages = [];
  }

  handleIncomingMessage(message: any) {
      if (this.userRole === 'ROLE_ADMIN' && !this.selectedChatUserId) {
      this.selectedChatUserId = message.senderId;
      this.adminView = 'chat'; // switch to chat view
      if (this.selectedChatUserId !== null) {
        this.loadHistory(this.selectedChatUserId);
      }
    }
    
    if (message.senderId !== this.currentUserId) {
      this.messages = [...this.messages, message]; 
      this.cdr.detectChanges(); // force change detection
      this.scrollToBottom();
    }
      if (this.userRole !== 'ROLE_ADMIN' && message.senderId !== this.currentUserId) {
        if (this.currentUserId) {
          this.loadHistory(this.currentUserId);
        }
      }
    }

  loadHistory(userId: number) {
    this.http.get<any[]>(`http://localhost:8080/api/v1/chat/history/${userId}`)
      .subscribe(data => {
        this.messages = data;
        this.scrollToBottom();
        this.cdr.detectChanges();
      });
  }

  subscribeToMessages() {
    if (this.userRole === 'ROLE_ADMIN') {
      this.socketService.subscribeToTopic('/topic/admin/messages', (message: any) => {
        this.handleIncomingMessage(message);
      });
    } else {
      this.socketService.subscribeToTopic(`/topic/user/${this.currentUserId}`, (message: any) => {
        this.handleIncomingMessage(message);
      });
    }
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const isToAdmin = this.userRole !== 'ROLE_ADMIN';
    const destination = isToAdmin ? '/app/chat.sendMessage' : '/app/chat.adminResponse';
    
    const messagePayload = {
      senderId: this.currentUserId,
      content: this.newMessage,
      receiverId: isToAdmin ? null : this.selectedChatUserId 
    };

    this.socketService.sendMessage(destination, messagePayload);

    this.messages.push({
      senderId: this.currentUserId,
      content: this.newMessage,
      timestamp: new Date()
    });

    this.newMessage = '';
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }
  
  isMyMessage(msg: any): boolean {
      console.log('Poredim:', msg.senderId, 'sa', this.currentUserId);
    return Number(msg.senderId) === Number(this.currentUserId);
  }

  isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
