import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../../env/environment';


describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should POST FormData to /auth/register with skip header and responseType text', () => {
    const formData = new FormData();
    formData.append('email', 'test@example.com');

    let res: string | undefined;

    service.register(formData).subscribe(r => (res = r));

    const req = httpMock.expectOne(environment.apiHost + '/auth/register');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBe(formData);
    expect(req.request.headers.get('skip')).toBe('true');
    expect(req.request.headers.get('Content-Type')).toBeNull(); // ne setuje se ručno
    expect(req.request.responseType).toBe('text');

    req.flush('User registered!');
    expect(res).toBe('User registered!');
  });
});
