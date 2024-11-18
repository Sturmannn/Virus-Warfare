package com.virus_warfare.client.models;

import com.virus_warfare.server.models.Response;

public interface ServerResponseListener {
    void onResponseReceived(Response response);
}
