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
package io.openliberty.guides.catalog;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.openliberty.guides.item.model.Item;
import io.openliberty.guides.item.model.ItemList;

@ApplicationScoped
public class CatalogManager {

    private List<Item> products = new LinkedList<>();

    public void createItem(String name, double price) {
        Item newProduct = new Item(name, price);

        if (!products.contains(newProduct)) {
            products.add(newProduct);
        }
    }

    public void updateItem(String name, double newPrice) {

        for (Item product : products) {
            if (product.getName().equals(name)) {
                products.remove(product);
                product.setPrice(newPrice);
                products.add(product);
            }
        }
    }

    public void removeItem(String name) {

        for (Item product : products) {
            if (product.getName().equals(name)) {
                products.remove(product);
            }
        }
    }

    // tag::list[]
    public ItemList listItems() {
        return new ItemList(products);
    }
    // end::list[]

}
