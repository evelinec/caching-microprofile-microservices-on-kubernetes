// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.item.model;

import java.util.List;

public class ItemList {

    private List<Item> items;

    public ItemList(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getTotal() {
        return items.size();
    }
}