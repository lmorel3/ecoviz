/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule, PreloadAllModules } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SimpleModalModule } from 'ngx-simple-modal';
import { TagInputModule } from 'ngx-chips';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { LeafletMarkerClusterModule } from '@asymmetrik/ngx-leaflet-markercluster';
import { NgSelectModule } from '@ng-select/ng-select';
import { AngularFileUploaderModule } from "angular-file-uploader";

/*
 * Platform and Environment providers/directives/pipes
 */
import { environment } from 'environments/environment';
import { ROUTES } from './app.routes';
// App is our top level component
import { APP_RESOLVER_PROVIDERS } from './app.resolver';
import { AppState, InternalStateType } from './app.service';

import { AppComponent } from './app.component';

import { HomeComponent } from './components/home';
import { MembersComponent } from './components/members';
import { TagsModalComponent } from './shared/tags-modal';
import { LoginComponent } from './components/login';
import { LinkComponent } from './components/members/link/link.component';
import { EditModalComponent } from './components/home/edit-modal/edit-modal.component';
import { NoContentComponent } from './components/no-content';
import { GraphComponent, TreeComponent } from './shared/graph';
import { AddressLookupComponent } from './shared/address-lookup';

import { FilterTagsPipe } from './pipes/filter-tags';

import { ProjectService } from './services/project-service';
import { OrganizationService } from './services/organization-service';
import { UserService } from './services/user-service';
import { DataService } from './services/data-service';
import { TagService } from './services/tag-service';
import { AuthService } from './services/auth-service';

import { AuthInterceptor } from './interceptors';

import '../styles/styles.scss';
import '../styles/headings.css';
import { defaultSimpleModalOptions } from 'ngx-simple-modal/dist/simple-modal/simple-modal-options';
import { LoginActivate } from './app.login.provider';

// Application wide providers
const APP_PROVIDERS = [
  AppState
];

interface StoreType {
  state: InternalStateType;
  restoreInputValues: () => void;
  disposeOldHosts: () => void;
}

/**
 * `AppModule` is the main entry point into Angular2's bootstraping process
 */
@NgModule({
  bootstrap: [ AppComponent ],
  declarations: [
    AppComponent,
    HomeComponent,
    NoContentComponent,
    GraphComponent,
    TreeComponent,
    MembersComponent,
    TagsModalComponent,
    LoginComponent,
    LinkComponent,
    AddressLookupComponent,
    EditModalComponent,
    FilterTagsPipe
  ],
  /**
   * Import Angular's modules.
   */
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    NgSelectModule,
    HttpClientModule,
    TagInputModule,
    RouterModule.forRoot(ROUTES, {
      useHash: Boolean(history.pushState) === false,
      preloadingStrategy: PreloadAllModules
    }),
    SimpleModalModule.forRoot({container: 'modal-container'}, {...defaultSimpleModalOptions, ...{
      closeOnEscape: true,
      closeOnClickOutside: true
    }}),
    LeafletModule.forRoot(),
    LeafletMarkerClusterModule.forRoot(),
    AngularFileUploaderModule,
    /**
     * This section will import the `DevModuleModule` only in certain build types.
     * When the module is not imported it will get tree shaked.
     * This is a simple example, a big app should probably implement some logic
     */
    ...environment.showDevModule ? [ ] : [],
  ],
  //Don't forget to add the component to entryComponents section
  entryComponents: [
    TagsModalComponent,
    LinkComponent,
    EditModalComponent
  ],
  /**
   * Expose our Services and Providers into Angular's dependency injection.
   */
  providers: [
    environment.ENV_PROVIDERS,
    APP_PROVIDERS,
    ProjectService,
    OrganizationService,
    DataService,
    TagService,
    UserService,
    AuthService,
    LoginActivate,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }    
  ]
})
export class AppModule {}
