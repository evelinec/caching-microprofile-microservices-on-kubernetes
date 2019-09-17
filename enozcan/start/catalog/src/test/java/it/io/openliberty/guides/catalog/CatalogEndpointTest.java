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
// tag::testClass[]
package it.io.openliberty.guides.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CatalogEndpointTest {

    private static String port;
    private static String baseUrl;

    private Client client;

    private static final String NAME = "shoes";
    private static final String PRICE = "20.0";
    private static final String NEWPRICE = "10.0";

    private final String CATALOG = "catalog/items";
    private final String CATALOG_CREATE = CATALOG + "/" + NAME + "/" + PRICE;
    private final String CATALOG_UPDATE = CATALOG + "/" + NAME + "/" + NEWPRICE;
    private final String CATALOG_DELETE = CATALOG + "/" + NAME;

    // Customer service
    private final String CUSTOMER = "customer";
    private static String cusPort;
    private static String baseCusUrl;

    // tag::BeforeClass[]
    @BeforeClass
    // end::BeforeClass[]
    // tag::oneTimeSetup[]
    public static void oneTimeSetup() {
        port = System.getProperty("default.http.port");
        baseUrl = "http://localhost:" + port + "/";

        cusPort = System.getProperty("customer.http.port");
        baseCusUrl = "http://localhost:" + cusPort + "/";
    }
    // end::oneTimeSetup[]

    // tag::Before[]
    @Before
    // end::Before[]
    // tag::setup[]
    public void setup() {
        client = ClientBuilder.newClient();
        // tag::JsrJsonpProvider[]
        client.register(JsrJsonpProvider.class);
        // end::JsrJsonpProvider[]
    }
    // end::setup[]

    // tag::After[]
    @After
    // end::After[]
    // tag::teardown[]
    public void teardown() {
        client.close();
    }
    // end::teardown[]

    // tag::tests[]
    // tag::test[]
    @Test
    // end::test[]
    // tag::testSuite[]
    public void testSuite() throws InterruptedException, ExecutionException {
        this.testEmptyCatalog();
        this.testCreate();
        this.testUpdate(); // This test depends on the previous one
        this.testDelete(); // This test depends on the previous one
    }
    // end::testSuite[]

    // tag::testEmptyCatalog[]
    public void testEmptyCatalog() {

        // Verify catalog microservice
        System.out.println("baseUrl: " + baseUrl);
        System.out.println("baseUrl + CATALOG: " + baseUrl + CATALOG);

        Response catResponse = this.getResponse(baseUrl + CATALOG);
        this.assertResponse(baseUrl + CATALOG, catResponse);

        JsonObject obj = catResponse.readEntity(JsonObject.class);
        System.out.println("testEmptyCatalog: " + obj);

        int expected = 0;
        int actual = obj.getInt("total");
        assertEquals("The catalog should be empty on application start but it wasn't",
                        expected, actual);

        catResponse.close();

        // Verify customer microservice
        Response cusResponse = this.getResponse(baseCusUrl + CUSTOMER);
        this.assertResponse(baseCusUrl + CUSTOMER, cusResponse);

        JsonObject cusObj = cusResponse.readEntity(JsonObject.class);
        System.out.println("testEmptyCatalog for customer: " + cusObj);

        int cusActual = cusObj.getInt("total");
        assertEquals("The catalog should be empty on application start but it wasn't",
                        expected, cusActual);

        cusResponse.close();
    }
    // end::testEmptyCatalog[]

    // tag::testCreate[]
    public void testCreate() throws InterruptedException, ExecutionException {

        // Create item
        System.out.println("baseUrl + CATALOG_CREATE: " + baseUrl + CATALOG_CREATE);
        Form form = new Form().param(NAME, PRICE);
        Response createResponse = client.target(baseUrl + CATALOG_CREATE).request()
                        .post(Entity.form(form));
        this.assertResponse(baseUrl + CATALOG_CREATE, createResponse);

        // Test the created item for catalog microservice
        Response catResponse = this.getResponse(baseUrl + CATALOG);
        this.assertResponse(baseUrl + CATALOG, catResponse);

        JsonObject obj = catResponse.readEntity(JsonObject.class);
        System.out.println("testCreate: " + obj);

        int expected = 1;
        int actual = obj.getInt("total");
        assertEquals("The catalog should have one entry,", expected, actual);

        boolean itemExists = obj.getJsonArray("items").getJsonObject(0).get("name")
                        .toString().contains(NAME);
        assertTrue("An item was created, but it was not " + NAME, itemExists);

        boolean priceIsRight = obj.getJsonArray("items").getJsonObject(0).get("price")
                        .toString().contains(PRICE);
        assertTrue("An item was created, but its price was not right", priceIsRight);

        createResponse.close();
        catResponse.close();

        // Test the created item for customer microservice
        Response cusResponse = this.getResponse(baseCusUrl + CUSTOMER);
        this.assertResponse(baseCusUrl + CUSTOMER, cusResponse);

        JsonObject cusObj = cusResponse.readEntity(JsonObject.class);
        System.out.println("testCreate for customer microservice: " + cusObj);

        int cusActual = cusObj.getInt("total");
        assertEquals("The catalog should have one entry,", expected, cusActual);

        boolean cusItemExists = cusObj.getJsonArray("items").getJsonObject(0)
                        .get("name").toString().contains(NAME);
        assertTrue("An item was created, but it was not " + NAME, cusItemExists);

        boolean cusPriceIsRight = cusObj.getJsonArray("items").getJsonObject(0)
                        .get("price").toString().contains(PRICE);
        assertTrue("An item was created, but its price was not right", cusPriceIsRight);

        cusResponse.close();

    }
    // end::testCreate[]

    // tag::testUpdate[]
    public void testUpdate() throws InterruptedException, ExecutionException {

        // Update the item created in the previous test
        System.out.println("baseUrl + CATALOG_UPDATE: " + baseUrl + CATALOG_UPDATE);
        Form form = new Form().param(NAME, NEWPRICE);
        Response updateResponse = client.target(baseUrl + CATALOG_UPDATE).request()
                        .put(Entity.form(form));
        this.assertResponse(baseUrl + CATALOG_UPDATE, updateResponse);

        // Test the updated item
        Response catResponse = this.getResponse(baseUrl + CATALOG);
        this.assertResponse(baseUrl + CATALOG, catResponse);

        JsonObject obj = catResponse.readEntity(JsonObject.class);
        System.out.println("testUpdate: " + obj);

        int expected = 1;
        int actual = obj.getInt("total");
        assertEquals("The catalog should have one entry,", expected, actual);

        boolean itemExists = obj.getJsonArray("items").getJsonObject(0).get("name")
                        .toString().contains(NAME);
        assertTrue("An item exits, but it was not " + NAME, itemExists);

        boolean priceIsRight = obj.getJsonArray("items").getJsonObject(0).get("price")
                        .toString().contains(NEWPRICE);
        assertTrue("An item was updated, but its price was not right", priceIsRight);

        catResponse.close();

        // Test the updated item for customer microservice
        Response cusResponse = this.getResponse(baseCusUrl + CUSTOMER);
        this.assertResponse(baseCusUrl + CUSTOMER, cusResponse);

        JsonObject cusObj = cusResponse.readEntity(JsonObject.class);
        System.out.println("testUpdate for customer microservice: " + cusObj);

        int cusActual = cusObj.getInt("total");
        assertEquals("The catalog should have one entry,", expected, cusActual);

        boolean cusItemExists = cusObj.getJsonArray("items").getJsonObject(0)
                        .get("name").toString().contains(NAME);
        assertTrue("An item exits, but it was not " + NAME, cusItemExists);

        boolean cusPriceIsRight = cusObj.getJsonArray("items").getJsonObject(0)
                        .get("price").toString().contains(NEWPRICE);
        assertTrue("An item was created, but its price was not right", cusPriceIsRight);

        cusResponse.close();

    }
    // end::testUpdate[]

    // tag::testDelete[]
    public void testDelete() throws InterruptedException, ExecutionException {

        System.out.println("baseUrl + CATALOG_DELETE: " + baseUrl + CATALOG_DELETE);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + CATALOG_DELETE);
        target.request().delete();

        // Test the item was deleted
        Response catResponse = this.getResponse(baseUrl + CATALOG);
        this.assertResponse(baseUrl + CATALOG, catResponse);

        JsonObject obj = catResponse.readEntity(JsonObject.class);
        System.out.println("testDelete: " + obj);

        int expected = 0;
        int actual = obj.getInt("total");
        assertEquals("The catalog should be empty,", expected, actual);

        catResponse.close();

        // Test the deleted item for customer microservice
        Response cusResponse = this.getResponse(baseCusUrl + CUSTOMER);
        this.assertResponse(baseCusUrl + CUSTOMER, cusResponse);

        JsonObject cusObj = cusResponse.readEntity(JsonObject.class);
        System.out.println("testUpdate for customer microservice: " + cusObj);

        int cusActual = cusObj.getInt("total");
        assertEquals("The catalog should have one entry,", expected, cusActual);

        cusResponse.close();

    }
    // end::testDelete[]

    private Response getResponse(String url) {
        return client.target(url).request().get();
    }

    private void assertResponse(String url, Response response) {
        assertEquals("Incorrect response code from " + url, 200, response.getStatus());
    }
}
// end::testClass[]