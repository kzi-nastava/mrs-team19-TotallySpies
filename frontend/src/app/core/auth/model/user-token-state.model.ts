export interface UserTokenStateDTO {  //matches backend login response dto
  accessToken: string;
  expirationDate: Date; 
}