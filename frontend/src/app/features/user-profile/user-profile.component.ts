import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  UserProfileResponseDTO,
  UserProfileUpdateRequestDTO,
  ProfileChangeRequestDTO,
  DriverActivityResponseDTO,
  VehicleInfoResponseDTO,
  DriverBlockedStatusDTO,
  AdminUserDTO
} from '../../shared/models/users/user.model';
import { UserService } from '../../shared/services/user.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../shared/services/admin.service';
import { DriverService } from '../../shared/services/driver.service';
import { AuthService } from '../../core/auth/services/auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { BlockDialogComponent } from '../../shared/components/block-dialog/block-dialog.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatSnackBarModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
})
export class UserProfileComponent {
  profileForm!: FormGroup;
  editingField: string | null = null;
  userRole: string = ''; // 'ROLE_ADMIN', 'ROLE_DRIVER', 'ROLE_USER'
  activeTab: string = 'approvals';

  profileImageUrl: string = '';

  user: UserProfileResponseDTO = {
    name: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: '',
    profilePicture: ''
  };

  // Admin podaci
  pendingRequests: ProfileChangeRequestDTO[] = [];
  driversList: AdminUserDTO[] = [];
  usersList: AdminUserDTO[] = [];

  // Driver podaci
  blockedStatus!: DriverBlockedStatusDTO;


  driverActivity?: DriverActivityResponseDTO;
  vehicleInfo?: VehicleInfoResponseDTO;

  constructor(
    private userService: UserService,
    private adminService: AdminService,
    private driverService: DriverService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) { }

  ngOnInit() {
    this.initForm();
    this.userRole = this.authService.getRole();
    console.log('Trenutna rola ulogovanog korisnika je:', this.userRole);
    this.loadDataByRole();

    if (this.userRole === 'ROLE_ADMIN' && this.activeTab === 'approvals') {
      this.loadPendingRequests();
    }
  }

  initForm() {
    this.profileForm = new FormGroup({
      name: new FormControl('', [Validators.required]),
      lastName: new FormControl('', [Validators.required]),
      phoneNumber: new FormControl(''),
      address: new FormControl('')
    });
  }

  loadDataByRole() {
    // Osnovni profil za sve
    this.userService.getProfile().subscribe(profile => {
      this.user = profile;
      this.profileForm.patchValue(profile);
      this.updateProfileImageUrl();

    });
    this.cdr.detectChanges();
    // Specifični podaci
    if (this.userRole === 'ROLE_ADMIN') {
      this.adminService.getPendingRequests().subscribe(res => this.pendingRequests = res);
      this.loadPendingRequests();
      this.adminService.getDriversList().subscribe(res => this.driversList = res);
      this.adminService.getUsersList().subscribe(res => this.usersList = res);
    } else if (this.userRole === 'ROLE_DRIVER') {
      this.driverService.getVehicleInfo().subscribe(res => {
        this.vehicleInfo = res;
        this.cdr.detectChanges();
      });
      this.driverService.getActivity().subscribe(res => {
        this.driverActivity = res;
        console.log(this.driverActivity.minutesLast24h)
        this.cdr.detectChanges();
      });

      this.driverService.getBlockedStatus().subscribe({
        next: (res) => this.blockedStatus = res,
        error: err => console.error(err)
      })

      this.cdr.detectChanges();
    }
  }

  loadPendingRequests() {
    this.adminService.getPendingRequests().subscribe(res => {
      this.pendingRequests = res;
      this.cdr.detectChanges();
    });
  }

  loadAdminLists() {
    this.adminService.getDriversList().subscribe(res => this.driversList = res);
    this.adminService.getUsersList().subscribe(res => this.usersList = res);
  }

  updateProfileImageUrl() {
    const defaultUrl = `http://localhost:8080/api/v1/users/image/default-profile-image.jpg`;

    if (!this.user.profilePicture) {
      this.profileImageUrl = defaultUrl;
      return;
    }

    const pic = this.user.profilePicture.trim().toLowerCase();

    // ako backend vraća "[null]", "null" ili prazan string
    if (!pic || pic === 'null' || pic === '[null]') {
      this.profileImageUrl = defaultUrl;
      return;
    }

    const fileName = this.user.profilePicture.split('/').pop();
    this.profileImageUrl =
      `http://localhost:8080/api/v1/users/image/${fileName}`;
  }

  saveProfile() {
    if (this.profileForm.valid) {
      const request: UserProfileUpdateRequestDTO = this.profileForm.value;

      this.userService.updateProfile(request).subscribe({
        next: () => {
          this.editingField = null;
          this.loadDataByRole();
          this.cdr.detectChanges();
        },
        error: () => {
          this.editingField = null;
        }
      });
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.userService.uploadProfileImage(file).subscribe({
        next: () => {
          this.loadDataByRole();
        },
        error: (err) => console.error('Upload failed', err)
      });
    }
  }

  // Admin akcije
  approveReq(id: number) {
    this.adminService.approveRequest(id).subscribe({
      next: () => {
        //alert('Zahtev je uspešno prihvaćen.');
        // Povuci ponovo sve pending zahteve
        this.snackBar.open(
          'Request approved successfully!',
          'OK',
          {
            duration: 4000,
            panelClass: ['confirm-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          }
        );
        this.loadPendingRequests();
      },
      error: () => {
        //alert('Došlo je do greške prilikom prihvatanja zahteva.');
        this.snackBar.open(
          'Failed to approve request.',
          'OK',
          {
            duration: 4000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          }
        );
      }
    });
  }

  rejectReq(id: number) {
    this.adminService.rejectRequest(id).subscribe({
      next: () => {
        //alert('Zahtev je odbijen.');
        // Povuci ponovo sve pending zahteve
        this.snackBar.open(
          'Request rejected successfully',
          'OK',
          {
            duration: 4000,
            panelClass: ['confirm-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          }
        );
        this.loadPendingRequests();
      },
      error: () => {
        //alert('Došlo je do greške prilikom odbijanja zahteva.');
        this.snackBar.open(
          'Failed to reject request.',
          'OK',
          {
            duration: 4000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          }
        );
      }
    });
  }

  formatMinutes(totalMinutes?: number): string {
    if (!totalMinutes || totalMinutes <= 0) {
      return '0h 0min';
    }

    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;

    return `${hours}h ${minutes}min`;
  }

  isImageField(field: string): boolean {
    return field === 'IMAGE';
  }

  getImageUrl(newValue: string): string {
    if (!newValue) {
      return 'http://localhost:8080/api/v1/users/image/default-profile-image.jpg';
    }

    const fileName = newValue.split('/').pop();
    return `http://localhost:8080/api/v1/users/image/${fileName}`;
  }

  toggleBlock(user: AdminUserDTO) {
    const dialogRef = this.dialog.open(BlockDialogComponent, {
      width: 'auto',
      data: { 
        isBlocked: user.blocked, 
        name: user.name 
      },
      panelClass: 'custom-dialog-container' // Opciono za uklanjanje default paddinga
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.confirm) {
        // Korisnik je kliknuo na dugme za potvrdu u dijalogu
        
        if (user.blocked) {
          // LOGIKA ZA ODBLOKIRANJE (UNBLOCK)
          this.adminService.unblockUser(user.id).subscribe({
            next: () => {
              user.blocked = false;
              user.blockReason = "";
              this.showSnackBar('User unblocked successfully!', 'success');
              this.cdr.detectChanges();
            },
            error: err => this.showSnackBar(err.error.message || 'Error unblocking user', 'error')
          });

        } else {
          // LOGIKA ZA BLOKIRANJE (BLOCK)
          const reason = result.reason;
          this.adminService.blockUser(user.id, reason).subscribe({
            next: () => {
              user.blocked = true;
              user.blockReason = reason;
              this.showSnackBar('User blocked successfully!', 'success');
              this.cdr.detectChanges();
            },
            error: err => this.showSnackBar(err.error.message || 'Error blocking user', 'error')
          });
        }
      }
    });
    this.cdr.detectChanges();
  }
  
  private showSnackBar(message: string, type: 'success' | 'error') {
    this.snackBar.open(message, 'OK', {
      duration: 4000,
      panelClass: type === 'success' ? ['confirm-snackbar'] : ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }
}
