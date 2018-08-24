/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Tag } from './tag.model';
import { Location } from './location'

export class Partner {

    id: string;
    name: string;
    country: string;
    role: string;
    location: Location;
    tags: Tag[];

    memberId: string;

}
