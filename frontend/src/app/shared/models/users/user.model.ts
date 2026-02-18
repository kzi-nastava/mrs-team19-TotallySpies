// Enum mapiranje
export enum VehicleType {
    STANDARD = 'STANDARD',
    LUXURIOUS = 'LUXURIOUS',
    VAN = 'VAN'
}

// Interfejs za polja koja se mijenjaju (ProfileField enum u Javi)
export type ProfileField = 'NAME' | 'LAST_NAME' | 'PHONE' | 'ADDRESS' | 'PROFILE_PICTURE';

export interface UserProfileResponseDTO {
    name: string,
    lastName: string,
    email: string,
    phoneNumber: string,
    address: string,
    profilePicture: string
}

export interface UserProfileUpdateRequestDTO {
    name: string,
    lastName: string,
    phoneNumber: string,
    address: string
}

export interface ChangePasswordRequestDTO {
    currentPassword: string,
    newPassword: string,
    confirmNewPassword: string
}

export interface DriverActivityResponseDTO {
    minutesLast24h: number;
}

export interface VehicleInfoResponseDTO {
    model: string;
    vehicleType: VehicleType;
    licensePlate: string;
    passengerCapacity: number;
    babyTransport: boolean;
    petTransport: boolean;
}

export interface ProfileChangeRequestDTO {
    id: number;
    userEmail: string;
    field: ProfileField; 
    oldValue: string;
    newValue: string;
    createdAt: string; 
}

export interface CreateDriverRequestDTO {
    // driver info
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    address: string;

    // vehicle info
    model: string;
    type: VehicleType; 
    licensePlate: string;
    seats: number;
    babyFriendly: boolean;
    petFriendly: boolean;
}

export interface DriverBlockedStatusDTO {
  blocked: boolean;
  blockReason: string | null;
}

export interface AdminUserDTO {
    id: number,
    email: string,
    name: string,
    role: string,
    blocked: boolean,
    blockReason: string
}
export interface UserDTO{
    id : number,
    role : string, 
    name : string,
    lastName : string,
    email : string
}
