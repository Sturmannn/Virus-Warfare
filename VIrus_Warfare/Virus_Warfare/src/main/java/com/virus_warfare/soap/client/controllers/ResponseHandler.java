package com.virus_warfare.soap.client.controllers;

import com.virus_warfare.soap.server.Response;

@FunctionalInterface
public interface ResponseHandler {
    void handleResponse(Response response);
}
