package com.virus_warfare.sockets.client.models;

import com.virus_warfare.sockets.server.models.Response;

public interface ServerResponseListener {
    void onResponseReceived(Response response);
}
