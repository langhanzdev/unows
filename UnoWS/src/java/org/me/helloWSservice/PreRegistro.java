/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.helloWSservice;

/**
 *
 * @author langhanz
 */
public class PreRegistro {
    
    private int id;
    private String nome;
    private int partida;

    public PreRegistro(int id, String nome, int partida) {
        this.id = id;
        this.nome = nome;
        this.partida = partida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPartida() {
        return partida;
    }

    public void setPartida(int partida) {
        this.partida = partida;
    }
    
    
}
