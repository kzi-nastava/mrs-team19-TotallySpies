import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

//interceptor's job is to automatically attach the JWT token to outgoing HTTP requests so
// it is not needed to do it manually in every service
//it is between angular app and backend and sees every request
//it is registered in app.config
@Injectable()
export class Interceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken: any = localStorage.getItem('token');
    if (req.headers.get('skip') === 'true') {  //dont attach token(for login, register)
      const cleanReq = req.clone({ headers: req.headers.delete('skip') });
      return next.handle(cleanReq);
    }

    if (accessToken) {
      //if token exists , put it in header
      const cloned = req.clone({
        headers: req.headers.set('Authorization', "Bearer " + accessToken), 
      });
      return next.handle(cloned);
    } else {
      return next.handle(req);
    }
  }
}
