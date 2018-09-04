package com.eco.resources;

import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.core.ChangeOrderData;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by neo on 9/3/18.
 */
public class ChangeOrderTest {
    private static final int READ_TIMEOUT = 20 * 1000; // second

    private ChangeOrderData changeOrderData = new ChangeOrderData("test comment", new ArrayList<String>());
    private ChangeOrder testChangeOrder = new ChangeOrder("testID", "testAuthor", changeOrderData);

    @ClassRule
    public static final DropwizardAppRule<ECOConfiguration> RULE =
            new DropwizardAppRule<ECOConfiguration>(ECORevisionControlService.class,
                    ResourceHelpers.resourceFilePath("ECORevisionControl.yml"));
    private static Client client;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("ChangeOrder test client");
            client.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        }

        Response response = client
                .target(Dict.API_V1_PATH)
                .path(testChangeOrder.getId())
                .request()
                .post(Entity.entity(testChangeOrder, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @After
    public void cleanUp() {
        Response response = client
                .target(Dict.API_V1_PATH)
                .path(testChangeOrder.getId())
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void update() throws IOException {
        ChangeOrder changeOrderCopy = objectMapper.readValue(objectMapper.writeValueAsString(testChangeOrder), ChangeOrder.class);
        changeOrderCopy.getData().setComment("modified comment");
        changeOrderCopy.getData().getBugNumbers().add("test bug");

        Response response = client
                .target(Dict.API_V1_PATH)
                .path(testChangeOrder.getId())
                .request()
                .put(Entity.entity(changeOrderCopy, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void get() throws JsonProcessingException {
        ChangeOrder changeOrder = client
                .target(Dict.API_V1_PATH)
                .path(testChangeOrder.getId())
                .request()
                .get(ChangeOrder.class);

        assertThat(objectMapper.writeValueAsString(changeOrder))
                .isEqualTo(objectMapper.writeValueAsString(testChangeOrder));
    }
}
