import { Injectable } from '@angular/core';
import {Observable} from "rxjs/index";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MappingRepoService {
  private baseUrl: string = 'http://localhost:8080/';
  private tableApi: string = this.baseUrl + 'tables';

  constructor(private http: HttpClient) { }

  getAllServiceMappings(): Observable<any> {
    return this.http.get(this.tableApi + '/services');
  }

  getAllPropertyMappings(): Observable<any> {
    return this.http.get(this.tableApi + '/properties');
  }

  addServiceMapping(serviceMapping: any): Observable<any> {
    return this.http.put(this.tableApi + '/' +  'services', serviceMapping);
  }

  addPropertyMapping(propertyMapping: any): Observable<any> {
    return this.http.put(this.tableApi + '/' +  'properties', propertyMapping);
  }

  resetAllServiceMappings(): Observable<any> {
    return this.http.delete(this.tableApi + '/' +  'services');
  }

  resetAllPropertyMappings(): Observable<any> {
    return this.http.delete(this.tableApi + '/' +  'properties');
  }
}
