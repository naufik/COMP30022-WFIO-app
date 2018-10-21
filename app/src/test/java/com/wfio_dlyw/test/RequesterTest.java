package com.wfio_dlyw.test;

import android.support.test.InstrumentationRegistry;

import com.android.volley.RequestQueue;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequesterTest {

    @Test
    public void testRequestAction() {
        Requester r = Requester.getInstance(InstrumentationRegistry.getContext());

        Mockito.mock(RequestQueue.class);
        r.requestAction(ServerAction.SERVER_TEST_ROOT, null, t -> {
            Assertions.fail("hello");
        });
    }
}
