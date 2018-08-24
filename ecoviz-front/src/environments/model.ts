import { NgModuleRef } from '@angular/core';

export interface Environment {
  production: boolean;
  ENV_PROVIDERS: any;
  showDevModule: boolean;
  apiUrl: string;
  decorateModuleRef(modRef: NgModuleRef<any>): NgModuleRef<any>;
}
