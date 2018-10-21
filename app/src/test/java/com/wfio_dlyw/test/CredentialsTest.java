package com.wfio_dlyw.test;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.navigation.wfio_dlyw.comms.Credentials;

import net.bytebuddy.asm.Advice;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

public class CredentialsTest {

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate() {
        Credentials credObj = new Credentials("something", "something else");

        Assertions.assertNotNull(credObj);
    }

    @Test
    public void testFailedCreation() {
        // if one of the parameters is null then an exception should be thrown
        Credentials cObj = null;

        try {
            cObj = new Credentials(null, null);
        } catch (NullPointerException e) {
            Assertions.assertNull(cObj);
        } catch (Exception e) {
            Assertions.fail("other kind of exception was thrown");
        }

        try {
            cObj = new Credentials(null, "AAA");
        } catch (NullPointerException e) {
            Assertions.assertNull(cObj);
        } catch (Exception e) {
            Assertions.fail("other kind of exception was thrown");
        }

        try {
            cObj = new Credentials("AAA", null);
        } catch (NullPointerException e) {
            Assertions.assertNull(cObj);
        } catch (Exception e) {
            Assertions.fail("other kind of exception was thrown");
        }
    }

    @Test
    public void testAllPropertyGetters() {
        String testUserName = "something@something.com";
        String testToken = "zimbabwe";
        Credentials cObj = new Credentials(testUserName, testToken);

        Assertions.assertEquals(testUserName, cObj.getEmail());
        Assertions.assertEquals(testToken, cObj.getPrivateToken());
    }

    @Test
    public void testJSONWellFormed() {
        String testUserName = "something@something.com";
        String testToken = "zimbabwe";
        Credentials cObj = new Credentials(testUserName, testToken);

        JSONObject t = cObj.toJSONObject();

        Assertions.assertTrue(t instanceof JSONObject);
    }
}
