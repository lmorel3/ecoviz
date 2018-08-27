/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import {
    Component,
    OnInit
  } from '@angular/core';
  
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
  
  @Component({

    selector: 'login',
    providers: [
    ],
    styleUrls: [ './login.component.scss' ],
    templateUrl: './login.component.html'
  })
  export class LoginComponent implements OnInit {
  
    private loginForm: FormGroup;
    
    constructor(
        private authService: AuthService,
        private router: Router
    ) {}
  
    public ngOnInit() {
        if(this.authService.isLogged()) {
            this.router.navigateByUrl('/');
        }

        this.loginForm = new FormGroup ({
            username: new FormControl('', Validators.required),
            password: new FormControl('', Validators.required)
        });
    }

    login() {
        let data = this.loginForm.value;
        this.authService.login(data.username, data.password).subscribe((tokenDto: any) => {
            if(tokenDto === null || tokenDto.token.length === 0) {
            } else {
                this.router.navigateByUrl('/');
            }
        });
    }
  
}
