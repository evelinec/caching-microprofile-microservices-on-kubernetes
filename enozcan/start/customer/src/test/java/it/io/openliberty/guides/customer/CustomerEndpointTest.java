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
package it.io.openliberty.guides.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerEndpointTest {

	private static String port;
	private static String baseUrl;

	private Client client;

	private final String CUSTOMER = "customer";

	// tag::BeforeClass[]
	@BeforeClass
	// end::BeforeClass[]
	// tag::oneTimeSetup[]
	public static void oneTimeSetup() {
		port = System.getProperty("customer.http.port");
		baseUrl = "http://localhost:" + port + "/";
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
		this.testEmptyList();
	}
	// end::testSuite[]

	// tag::testList[]
	public void testEmptyList() {
		System.out.println("baseUrl: " + baseUrl);
		System.out.println("baseUrl + CUSTOMER: " + baseUrl + CUSTOMER);

		Response response = this.getResponse(baseUrl + CUSTOMER);
		this.assertResponse(baseUrl + CUSTOMER, response);

		JsonObject obj = response.readEntity(JsonObject.class);
		System.out.println("testEmptyList: " + obj);

		int expected = 0;
		int actual = obj.getInt("total");
		assertEquals("The catalog should be empty on application start but it wasn't", expected, actual);

		response.close();
	}
	// end::testList[]

	private Response getResponse(String url) {
		return client.target(url).request().get();
	}

	private void assertResponse(String url, Response response) {
		assertEquals("Incorrect response code from " + url, 200, response.getStatus());
	}
}
// end::testClass[]