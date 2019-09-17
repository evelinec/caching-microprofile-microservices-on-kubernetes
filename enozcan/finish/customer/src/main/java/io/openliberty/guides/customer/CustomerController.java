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
package io.openliberty.guides.customer;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.openliberty.guides.item.model.Item;
import io.openliberty.guides.item.model.ItemList;

@ApplicationScoped
public class CustomerController {

	private HazelcastInstance instance = configureHazelcast();
	
	private List<Item> products = instance.getList("items");

    // tag::list[]
	public ItemList listItems() {
		return new ItemList(products);
    }
    // end::list[]

	private HazelcastInstance configureHazelcast(){
		Config config = new Config();
		config.getGroupConfig().setName("CatalogCluster");
		return Hazelcast.newHazelcastInstance(config);
	}
}