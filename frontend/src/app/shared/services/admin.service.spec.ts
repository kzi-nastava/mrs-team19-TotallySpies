import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AdminService } from './admin.service';
import { environment } from '../../../env/environment';
import { CreateDriverRequestDTO, VehicleType } from '../models/users/user.model';

describe('AdminService - createDriver', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST to /admin/create-driver with correct data', () => {
    const dto: CreateDriverRequestDTO = {
      email: 'driver@example.com',
      firstName: 'Marko',
      lastName: 'Markovic',
      address: 'Bulevar 1',
      phone: '+381 63 123 1234',
      model: 'Toyota Corolla',
      type: VehicleType.STANDARD,
      licensePlate: 'NS-123-AB',
      seats: 4,
      babyFriendly: false,
      petFriendly: true
    };

    service.createDriver(dto).subscribe();

    const req = httpMock.expectOne(`${environment.apiHost}/admin/create-driver`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush(null);
  });
});