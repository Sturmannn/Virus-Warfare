package com.virus_warfare.soap.shared;

import com.virus_warfare.soap.server.Response;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.UUID;

@WebService
public interface GameService {

    @WebMethod
    Response connect(String nickname);
    @WebMethod
    Response longPolling(UUID id);
    @WebMethod
    void makeMove(UUID id, int x, int y);
    @WebMethod
    void restartGame(UUID id);
    @WebMethod
    void skipMove(UUID id);
}