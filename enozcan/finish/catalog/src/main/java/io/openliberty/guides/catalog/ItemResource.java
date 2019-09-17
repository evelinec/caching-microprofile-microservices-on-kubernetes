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

// CDI
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.openliberty.guides.item.model.ItemList;

@RequestScoped
@Path("/items")
public class ItemResource {

    @Inject
    CatalogManager manager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "Items successfully retrieved from your catalog.")
    @Operation(summary = "Return an JsonObject instance which contains "
                    + "the items in your catalog.")
    public ItemList list() {
        return manager.listItems();
    }

    @POST
    @Path("{name}/{price}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "Item successfully added to your catalog.")
    @Operation(summary = "Add a new item to catalog.")
    public String create(
                    @Parameter(description = "Item you want to create in your catalog.", required = true) @PathParam("name") String name,
                    @Parameter(description = "Price for this item.", required = true) @PathParam("price") double price) {
        manager.createItem(name, price);
        return name + " - " + price + " is added to your catalog.";
    }

    @PUT
    @Path("{name}/{price}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "Item successfully updated in the catalog.")
    @Operation(summary = "Update an item in the catalog to a new price.")
    public String update(
                    @Parameter(description = "Item you want to update.", required = true) @PathParam("name") String name,
                    @Parameter(description = "New price for this item.", required = true) @PathParam("price") double newPrice) {
        manager.updateItem(name, newPrice);
        return name + " - " + newPrice + " is updated in your catalog.";
    }

    @DELETE
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "Item you want to delete from the catalog.")
    @Operation(summary = "Delete an item from the catalog.")
    public String remove(
                    @Parameter(description = "Item name you want to delete.", required = true) @PathParam("name") String name) {
        manager.removeItem(name);
        return name + " is deleted from your catalog.";

    }

}