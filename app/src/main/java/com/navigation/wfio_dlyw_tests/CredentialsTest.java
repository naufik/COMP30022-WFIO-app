package com.navigation.wfio_dlyw_tests;

import com.navigation.wfio_dlyw.comms.Credentials;

import org.json.JSONObject;
import org.junit.jupiter.api.*;

public class CredentialsTest {

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
        Assertions.assertEquals(testToken, cObj);
    }

    @Test
    public void testJSONWellFormed() {
        String testUserName = "something@something.com";
        String testToken = "zimbabwe";
        Credentials cObj = new Credentials(testUserName, testToken);

        JSONObject j = cObj.toJSONObject();
        Assertions.assertTrue(j.has("email"));
        Assertions.assertTrue(j.has("token"));
    }
}
