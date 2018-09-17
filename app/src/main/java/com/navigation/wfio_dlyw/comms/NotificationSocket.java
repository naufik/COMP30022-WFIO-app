package com.navigation.wfio_dlyw.comms;


import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class NotificationSocket {
    private Socket connection;

    public NotificationSocket(Credentials auth) {
        try {
            this.connection = IO.socket("http://rawon.naufik.net");
        } catch (Exception e) {

        }

        JSONObject newObj = auth.toJSONObject();
        try {
            newObj.put("action", "socket");
        } catch (Exception e) {

        }

        connection.send(newObj);

        connection.on("validate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        connection.on("notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
    }



}
