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

import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth-service';
import { UserService } from '../../../services/user-service';
import { User } from '../../../models/user.model';

@Component({
    selector: 'users',
    providers: [
    ],
    styles: [ '' ],
    templateUrl: './users-settings.component.html'
})
export class UsersSettingsComponent implements OnInit {
  
    private userForm: FormGroup;
    private userCreated = false;
    
    private users: User[];

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private router: Router
    ) {}
  
    public ngOnInit() {
        if(!this.authService.isAdmin()) {
            this.router.navigateByUrl('/');
        }

        this.initForm();
        this.loadUsers();
    }

    initForm() {
        this.userForm = new FormGroup ({
            username: new FormControl('', [Validators.required, Validators.minLength(3)]),
            password: new FormControl('', Validators.required),
            passwordConfirmation: new FormControl('', [Validators.required, this.checkConfirmPassword]),
            shouldMakeAdmin: new FormControl('')
        });
    }

    loadUsers() {
        this.userService.getUsers().subscribe((users: User[]) => this.users = users);
    }

    createUser() {
        let user = new User(this.userForm.value.username, this.userForm.value.password);
        user.roles = ['user'];

        if(this.userForm.value.shouldMakeAdmin) {
            user.roles.push('admin');
        }

        this.userService.createUser(user).subscribe(() => {
            this.userCreated = true;
            this.loadUsers()
        });
    }

    delete(user: User) {
        if(confirm('Are you sure?')) {
            this.userService.deleteUser(user.id)
                .subscribe(() => this.loadUsers(), () => alert('Unable to delete this user!'));
        }
    }

    private checkConfirmPassword(c: AbstractControl): any {
        if(!c.parent || !c) return;

        const password = c.parent.get('password');
        const passwordConfirmation = c.parent.get('passwordConfirmation')

        if(!password || !passwordConfirmation ) return;

        if (password.value !== passwordConfirmation .value) {
            return { invalid: true };
        }
    }

}
