/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.helloWSservice;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author langhanz
 */
@WebService(serviceName = "UnoWSservice")
public class UnoWSservice {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "test")
    public String test(@WebParam(name = "name") String txt) {
        return "Hello GGG TTTT  " + txt + " !";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "teste2")
    public String teste2(@WebParam(name = "tipo") String tipo) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "preRegistro")
    public int preRegistro(@WebParam(name = "j1Nome") String j1Nome, @WebParam(name = "j1Id") int j1Id, @WebParam(name = "j2Nome") String j2Nome, @WebParam(name = "j2Id") int j2Id) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "registraJogador")
    public int registraJogador(@WebParam(name = "nome") String nome) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "encerraPartida")
    public int encerraPartida(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "temPartida")
    public int temPartida(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemOponente")
    public String obtemOponente(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "ehMinhaVez")
    public int ehMinhaVez(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartasBaralho")
    public int obtemNumCartasBaralho(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartas")
    public int obtemNumCartas(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartasOponente")
    public int obtemNumCartasOponente(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "mostraMao")
    public int mostraMao(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemCartaMesa")
    public int obtemCartaMesa(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemCorAtiva")
    public int obtemCorAtiva(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "compraCarta")
    public int compraCarta(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "jogaCarta")
    public int jogaCarta(@WebParam(name = "idJogador") int idJogador, @WebParam(name = "carta") int carta, @WebParam(name = "cor") int cor) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemPontos")
    public int obtemPontos(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemPontosOponente")
    public int obtemPontosOponente(@WebParam(name = "idJogador") int idJogador) {
        //TODO write your implementation code here:
        return 0;
    }
    
    
}
