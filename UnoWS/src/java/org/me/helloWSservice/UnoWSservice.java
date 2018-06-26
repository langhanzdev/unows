/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.helloWSservice;

import java.util.ArrayList;
import java.util.Random;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author langhanz
 */
@WebService(serviceName = "UnoWSservice")
public class UnoWSservice {

    private String message;
    
    private int idPartidas = 0;
    private final int nrPartidas = 500;
    private Partida[] partidas = new Partida[nrPartidas];
    private ArrayList<PreRegistro> preRegistros = new ArrayList<>();
   

    
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
    public synchronized String teste2(@WebParam(name = "tipo") String tipo) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "preRegistro")
    public synchronized int preRegistro(@WebParam(name = "j1Nome") String j1Nome, @WebParam(name = "j1Id") int j1Id, @WebParam(name = "j2Nome") String j2Nome, @WebParam(name = "j2Id") int j2Id) {
        this.preRegistros.add(new PreRegistro(j1Id, j1Nome, idPartidas,1));
        this.preRegistros.add(new PreRegistro(j2Id, j2Nome, idPartidas,2));
        idPartidas++;
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "registraJogador")
    public synchronized int registraJogador(@WebParam(name = "nome") String nome) {
        
        PreRegistro aux = null;
        for(PreRegistro  pre:preRegistros){ //Percorre lista de pre registros
            if(pre.getNome().equals(nome)){ //encontrou jogador
                aux = pre;
                break;
            }
        }
        
        if(usuarioCasdastrado(nome)) //se ja está cadastrado
            return -1;

        Jogador jogador = new Jogador();
        jogador.setNome(nome);
        jogador.setId(aux.getId());
        return colocaNaPartida(jogador, aux.getPartida(), aux.getPosicao()); //return id ou -2
        
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "encerraPartida")
    public synchronized int encerraPartida(@WebParam(name = "idJogador") int idJogador) {
        
        int partida = encontraPartida(idJogador);
        int nrJogador = identificaJogador(partida, idJogador);
        if(partida > -1){
            if(nrJogador == 1){
                this.partidas[partida].setJogador1(null);
                if(this.partidas[partida].getJogador2() == null){
                    this.partidas[partida] = null;
                }
                return 0;
            }else{
                this.partidas[partida].setJogador2(null);
                if(this.partidas[partida].getJogador1() == null){
                    this.partidas[partida] = null;
                }
                return 0;
            }
        }
        return -1;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "temPartida")
    public synchronized int temPartida(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        
        if(partida == -1) return -1; //Não existe partida para esse jogador
        
        //Tempo aguardando outro jogador entrar
        long t = System.currentTimeMillis()-this.partidas[partida].getTempoAguardaJogador();
        
//        if(System.currentTimeMillis()-this.partidas[partida].getTempoAguardaJogador() > 120000){               
//            return -2;
//        }
        
        if(partida > -1){
            if(this.partidas[partida].getJogador1() == null || this.partidas[partida].getJogador2() == null){
                return 0; //Ainda não há partida
            }
            if(this.partidas[partida].getJogador1().getId() == idJogador){
                this.partidas[partida].preparaJogo();
                if(ehPular(this.partidas[partida].getTopoDescarte()) || ehInverter(this.partidas[partida].getTopoDescarte())){
                    if(this.partidas[partida].getVez() == 1)
                        this.partidas[partida].setVez(2);
                    else
                        this.partidas[partida].setVez(1);
                }
                return 1;
            }else if(this.partidas[partida].getJogador2().getId() == idJogador){
                this.partidas[partida].preparaJogo();
                
                return 2;
            }
        }
       
        return -1;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemOponente")
    public synchronized String obtemOponente(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        if(partida > -1){
            int nrJogador = identificaJogador(partida, idJogador);
            if(nrJogador == 1){
                if(this.partidas[partida].getJogador2() == null){
                    return "";
                }else{
                   return this.partidas[partida].getJogador2().getNome();
                }
            }else{
                if(this.partidas[partida].getJogador1() == null){
                    return "";
                }else{
                   return this.partidas[partida].getJogador1().getNome();
                }
            }
        }
        return "";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "ehMinhaVez")
    public synchronized int ehMinhaVez(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        int temPartida = temPartida(idJogador);
        
        if(temPartida == -1) return -6; //Perdedor por WO
        if(temPartida == -2) return 5; //Vencedor por WO
        if(temPartida == 0) return -2; //Não há dois jogadores
        
        if(partida > -1){

            int nrJogador = identificaJogador(partida, idJogador);
            
            //Tempo de espera da jogada
            if(nrJogador == 1){
                if(this.partidas[partida].getVez() == 2){
                    if(System.currentTimeMillis()-this.partidas[partida].getTempoAguardaJogada2() > 60000){
                        return 5; //Venceu por WO
                    }
                }
            }else{
                if(this.partidas[partida].getVez() == 1){
                    if(System.currentTimeMillis()-this.partidas[partida].getTempoAguardaJogada1() > 60000){
                        return 5; //Venceu por WO
                    }
                }
            }
            
            
            
            if(nrJogador == 1){ //Jogador 1 venceu
                if(this.partidas[partida].getJogador1().getCartas().isEmpty())
                    return 2;
                if(this.partidas[partida].getJogador2().getCartas().isEmpty())
                    return 3;
            }else{ //Jogador 2 venceu
                if(this.partidas[partida].getJogador2().getCartas().isEmpty())
                    return 2;
                if(this.partidas[partida].getJogador1().getCartas().isEmpty())
                    return 3;
            }
            
            //Acabou baralho
            if(this.partidas[partida].getNumCartas() == 0){
                // Empate
                if(obtemPontos(idJogador) == obtemPontosOponente(idJogador))
                    return 4; //empate
                
                //Pontos do jogador é maior que do oponente
                if(obtemPontos(idJogador) > obtemPontosOponente(idJogador)){
                    return 2; // venceu
                }else{
                    return 3; // perdeu
                }
            }
            
            
            if(nrJogador == this.partidas[partida].getVez()){
                return 1; //Sim
            }else{
                return 0; //Não
            }
        }
        return -1;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartasBaralho")
    public synchronized int obtemNumCartasBaralho(@WebParam(name = "idJogador") int idJogador) {
        if(temPartida(idJogador) == 0) return -2; //ainda não tem 2 jogadores
        int partida = encontraPartida(idJogador);
        if(this.partidas[partida].getBaralho() != null)
            return this.partidas[partida].getNumCartas(); //Num cartas do baralho
        return -1;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartas")
    public synchronized int obtemNumCartas(@WebParam(name = "idJogador") int idJogador) {
        if(temPartida(idJogador) == 0) return -2;//ainda nao tem 2 jogadores
        int partida = encontraPartida(idJogador);
        int nrJogador = identificaJogador(partida, idJogador);
        if(nrJogador == 1){ //num cartas do jogador
            return this.partidas[partida].getJogador1().getCartas().size();
        }
        if(nrJogador == 2){
            return this.partidas[partida].getJogador2().getCartas().size();
        }
        return -1; //erro
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemNumCartasOponente")
    public synchronized int obtemNumCartasOponente(@WebParam(name = "idJogador") int idJogador) {
        if(temPartida(idJogador) == 0) return -2;//nao tem 2 jogadores
        int partida = encontraPartida(idJogador);
        int nrJogador = identificaJogador(partida, idJogador);
        if(nrJogador == 1){ //num cartas oponente
            return this.partidas[partida].getJogador2().getCartas().size();
        }
        if(nrJogador == 2){
            return this.partidas[partida].getJogador1().getCartas().size();
        }
        return -1;//erro
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "mostraMao")
    public synchronized String mostraMao(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        int nrJogador = identificaJogador(partida, idJogador);
        String mao = "";
        ArrayList<Integer> cartasMao;
        if(nrJogador == 1){
            cartasMao = this.partidas[partida].getJogador1().getCartas();
        }else{
            cartasMao = this.partidas[partida].getJogador2().getCartas();
        }
        for(Integer i:cartasMao){
            mao += "|" + dicionarioCartas(i);
        }
        if(!mao.equals(""))
            mao = mao.substring(1);
        return mao;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemCartaMesa")
    public synchronized String obtemCartaMesa(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);

        if(partida > -1){
            return dicionarioCartas(this.partidas[partida].getTopoDescarte());//carta mesa
        }
        
        return "";//erro
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemCorAtiva")
    public synchronized int obtemCorAtiva(@WebParam(name = "idJogador") int idJogador) {
        if(temPartida(idJogador) == 0) return -2;//nao tem 2 jogadores
        int partida = encontraPartida(idJogador);
        if(partida > -1){
            System.out.println("RETURN "+this.partidas[partida].getCorAtiva()+ "Carta topo "+obtemCartaMesa(idJogador));
            
            return this.partidas[partida].getCorAtiva();//cor ativa
        }
        return -1; //erro
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "compraCarta")
    public synchronized int compraCarta(@WebParam(name = "idJogador") int idJogador) {
        
        int partida = encontraPartida(idJogador);
        if(temPartida(idJogador) == 0) return -2;//nao tem 2 jogadores
        
        if(partida == -1) return -1; //jogador nao encontrado
        if(ehMinhaVez(idJogador) == 0) return -3; //não é a vez do jogador
        //if(partida == 0) return -2;  
        

        int nrJogador = identificaJogador(partida, idJogador);
        this.partidas[partida].compraCarta(nrJogador);//compra
        if(nrJogador == 1) //e passa a vez
            this.partidas[partida].setVez(2);
        else
            this.partidas[partida].setVez(1);
        
        return 1;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "jogaCarta")
    public synchronized int jogaCarta(@WebParam(name = "idJogador") int idJogador, @WebParam(name = "carta") int indexCarta, @WebParam(name = "cor") int cor) {
        int carta;
        int temPartida = temPartida(idJogador);
        
        int partida = encontraPartida(idJogador);
        int nrJogador = identificaJogador(partida, idJogador);
        
        if(this.partidas[partida].getVez() != nrJogador) return -3; //Não é a vez do jogador
      
        if(indexCarta < 0 || indexCarta >= obtemNumCartas(idJogador)) return -4; //parametros invalidos
        
        if(nrJogador == 1){
            carta = this.partidas[partida].getJogador1().getCartas().get(indexCarta);
        }else{
            carta = this.partidas[partida].getJogador2().getCartas().get(indexCarta);
        }
        
        if(temPartida == -1) return -1;//jogador não encontrado
        if(temPartida == 0) return -2;//nao tem 2 jogadores
        if(this.partidas[partida].getVez() != nrJogador) return -3; //Não é a vez do jogador
        if(ehCoringa(carta) || ehMais4(carta))
            if(cor < 0 || cor > 3) return -4;//Parametros invalidos
        
        boolean mesmaCor = false;
        int corAtiva = 0;
        
        if(partida > -1){
            
            int topoDescarte = this.partidas[partida].getTopoDescarte(); 
            corAtiva = descobreCor(carta);
            //this.partidas[partida].setCorAtiva(corAtiva);
            System.out.println("COR "+this.partidas[partida].getCorAtiva()+" Carta "+topoDescarte);
            
            //Verifica se a cor eh compativel
            
            if(carta >=0 && carta <=24 && topoDescarte >=0 && topoDescarte <=24){ //Azul
                mesmaCor = true;
            }else
            if(carta >=25 && carta <=49 && topoDescarte >=25 && topoDescarte <=49){//Amarela 
                mesmaCor = true;
            }else
            if(carta >=50 && carta <=74 && topoDescarte >=50 && topoDescarte <=74){//Verde
                mesmaCor = true;
            }else
            if(carta >=75 && carta <=99 && topoDescarte >=75 && topoDescarte <=99){//Vermelha
                mesmaCor = true;
            }
            
            //Se eh da mesma cor
            if(mesmaCor){
                int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                if(ehMais2(carta)){
                    compraMais2(partida, nrJogador);
                }
                
                if(ehPular(carta)){
                    pula(partida, nrJogador);
                }
                
                if(ehInverter(carta)){
                    pula(partida, nrJogador);
                }
                
                return retornoJoga;
            }
            
            //Se a carta anterior era coringa
            if(topoDescarte >= 100 && topoDescarte <= 107){

                corAtiva = this.partidas[partida].getCorAtiva();
                int corCarta = qualCor(carta);
                //System.out.println("Cor carta "+corCarta+" Cor ativa: "+corAtiva);
                if(corCarta == corAtiva){
                    int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                    if(ehMais2(carta))
                        compraMais2(partida, nrJogador);
                    if(ehPular(carta))
                        pula(partida, nrJogador);
                    if(ehInverter(carta))
                        inverteVez(partida, nrJogador);
                    
                    return retornoJoga;
                }
                
                if(corAtiva != corCarta && !ehCoringa(carta) && !ehMais4(carta))
                    return 0;
            }
            
            //Se eh carta de ação sobre carta de ação
            if(ehMais2(carta) && ehMais2(topoDescarte)){
                int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                compraMais2(partida, nrJogador);
                return retornoJoga;
            }
            
            //Se eh carta de ação sobre carta de ação
            if(ehPular(carta) && ehPular(topoDescarte)){
                int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                pula(partida, nrJogador);
                return retornoJoga;
            }

            //Se eh carta de ação sobre carta de ação
            if(ehInverter(carta) && ehInverter(topoDescarte)){
                int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                inverteVez(partida, nrJogador);
                return retornoJoga;
            }
            
            //Se eh carta de ação sobre carta de ação
            if(ehCoringa(carta)){
                return this.partidas[partida].jogaCarta(indexCarta, cor, nrJogador);
            }
            
            //Se eh carta de ação sobre carta de ação
            if(ehMais4(carta)){
                int retornoJoga = this.partidas[partida].jogaCarta(indexCarta, cor, nrJogador);
                compraMais2(partida, nrJogador);
                compraMais2(partida, nrJogador);
                if(nrJogador == 1){
                    this.partidas[partida].setVez(2);
                }else{
                    this.partidas[partida].setVez(1);
                }
                return retornoJoga;
            }
            
            // SE NAO EH ACAO E NEM COR IGUAL, TESTA VALORES 
            
            int soma;
            
            if(carta % 2 == 0)
                soma = -1;
            else
                soma = 1;
            
            if((carta >= 25 && carta <= 49) || (carta >= 75 && carta <= 99)){
                soma = inverte(soma);
            }
            
            if(carta+soma == topoDescarte)
                return this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
            
            int cartaAux = carta;
            int proximo;
            if(carta < topoDescarte){
                proximo = 25;
            }else{
                proximo = -25;
            }
            
            while(cartaAux+proximo < 99 && cartaAux+proximo > 0){
                if(cartaAux+proximo == topoDescarte || cartaAux+proximo+soma == topoDescarte){
                    return this.partidas[partida].jogaCarta(indexCarta, corAtiva, nrJogador);
                }
                cartaAux += proximo;
                //soma = inverte(soma);
            }
           
        }
        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemPontos")
    public synchronized int obtemPontos(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        
        if(partida == -1) return -1;
        if(temPartida(idJogador) == -1) return -2; 
        
        int nrJogador = identificaJogador(partida, idJogador);
        int pontos = 0;
        
        ArrayList<Integer> mao;
        if(nrJogador == 2){
            mao = this.partidas[partida].getJogador2().getCartas();
        }else
            mao = this.partidas[partida].getJogador1().getCartas();
        
        
        for(Integer carta:mao){
            if(ehMais2(carta) || ehInverter(carta) || ehPular(carta)){
                pontos += 20;
                continue;
            }
            if(ehCoringa(carta) || ehMais4(carta)){
                pontos += 50;
                continue;
            }
            
            if(carta == 0 || carta == 25 || carta == 50 || carta == 75){
                continue;
            }
            
            int cor = qualCor(carta);
            int cartaAux = 1;
            
            switch(cor){
                case 0:
                    cartaAux = carta;
                    break;
                case 1:
                    cartaAux = carta-25;
                    break;
                case 2:
                    cartaAux = carta-50;
                    break;
                case 3:
                    cartaAux = carta-75;
                    break;
            }
            
            if(cartaAux%2 == 0){
                pontos += (cartaAux/2);
            }else{
                pontos += (cartaAux/2)+1;
            }
        }
        if(nrJogador == 2){
            this.partidas[partida].setPontosJogador1(pontos);
            return this.partidas[partida].getPontosJogador1();
        }else{
            this.partidas[partida].setPontosJogador2(pontos);
            return this.partidas[partida].getPontosJogador2();
        }
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "obtemPontosOponente")
    public synchronized int obtemPontosOponente(@WebParam(name = "idJogador") int idJogador) {
        int partida = encontraPartida(idJogador);
        
        if(partida == -1) return -1; //jogador nao encontrado
        if(temPartida(idJogador) == 0) return -2; //nao tem 2 jogadores
        
        int nrJogador = identificaJogador(partida, idJogador);
        int pontos = 0;
        
        ArrayList<Integer> mao;
        if(nrJogador == 1){
            mao = this.partidas[partida].getJogador2().getCartas();
        }else
            mao = this.partidas[partida].getJogador1().getCartas();
        
        
        for(Integer carta:mao){
            if(ehMais2(carta) || ehInverter(carta) || ehPular(carta)){
                pontos += 20;
                continue;
            }
            if(ehCoringa(carta) || ehMais4(carta)){
                pontos += 50;
                continue;
            }
            
            if(carta == 0 || carta == 25 || carta == 50 || carta == 75){
                continue;
            }
            
            int cor = qualCor(carta);
            int cartaAux = 1;
            
            switch(cor){
                case 0:
                    cartaAux = carta;
                    break;
                case 1:
                    cartaAux = carta-25;
                    break;
                case 2:
                    cartaAux = carta-50;
                    break;
                case 3:
                    cartaAux = carta-75;
                    break;
            }
            
            if(cartaAux%2 == 0){
                pontos += (cartaAux/2);
            }else{
                pontos += (cartaAux/2)+1;
            }
        }
        if(nrJogador == 1){
            this.partidas[partida].setPontosJogador1(pontos);
            return this.partidas[partida].getPontosJogador1();
        }else{
            this.partidas[partida].setPontosJogador2(pontos);
            return this.partidas[partida].getPontosJogador2();
        }
    }
    
    //#### Metodos auxiliares ################################################3
    
    /**
     * Verifica se a carta eh coringa (Cg/*)
     * @param carta
     * @return 
     */
    public boolean ehCoringa(int carta){
        if(carta >= 100 && carta <= 103)
            return true;
        return false;
    }
    
    /**
     * Verifica se a carta eh coringa +4 (C4/*)
     * @param carta
     * @return 
     */
    public boolean ehMais4(int carta){
        if(carta >= 104 && carta <= 107)
            return true;
        return false;
    }
    
    /**
     * Compra mais duas cartas para o jogador adversario e mantem a vez com o atual
     * @param partida
     * @param nrJogador 
     */
    public void compraMais2(int partida, int nrJogador){
        if(nrJogador == 1){
            this.partidas[partida].compraCarta(2);
            this.partidas[partida].compraCarta(2);
            this.partidas[partida].setVez(1);
        }else{
            this.partidas[partida].compraCarta(1);
            this.partidas[partida].compraCarta(1);
            this.partidas[partida].setVez(2);
        }
    }
    
    /**
     * Pula a vez do jogador adversario
     * @param partida
     * @param nrJogador 
     */
    public void pula(int partida, int nrJogador){
        if(nrJogador == 1)
            this.partidas[partida].setVez(1);
        else
            this.partidas[partida].setVez(2);
    }
    
    /**
     * Inverte (pula) vez do jogador
     * @param partida
     * @param nrJogador 
     */
    public void inverteVez(int partida, int nrJogador){
        if(nrJogador == 1)
            this.partidas[partida].setVez(1);
        else
            this.partidas[partida].setVez(2);
    }
    
    /**
     * Verifica se a carta eh um +2 (+2/Cor)
     * @param carta
     * @return 
     */
    public boolean ehMais2(int carta){
        if(carta == 23 || carta == 24 || carta == 49 || carta == 48 || carta == 74 || carta == 73 || carta == 98 || carta == 99)
            return true;
        return false;
    }
    
    /**
     * Verifica se a carta eh pular (Pu/cor)
     * @param carta
     * @return 
     */
    public boolean ehPular(int carta){
        if(carta == 19 || carta == 20 || carta == 44 || carta == 45 || carta == 60 || carta == 70 || carta == 94 || carta == 95)
            return true;
        return false;
    }
    
    /**
     * Verifica se a carta eh inverter (In/cor)
     * @param carta
     * @return 
     */
    public boolean ehInverter(int carta){
        if(carta == 21 || carta == 22 || carta == 46 || carta == 47 || carta == 71 || carta == 72 || carta == 96 || carta == 97)
            return true;
        return false;
    }
    
    /**
     * Retorna a cor da carta
     * @param carta
     * @return 
     */
    public int qualCor(int carta){
        if(carta >= 0 && carta <= 24)
            return 0;
        if(carta >= 25 && carta <= 49)
            return 1;
        if(carta >= 50 && carta <= 74)
            return 2;
        if(carta >= 75 && carta <= 99)
            return 3;
        return -1;
    }
    
    /**
     * Inverte soma para encontrar cartas
     * @param s
     * @return 
     */
    public int inverte(int s){
        if(s == 1)
            return -1;
        else
            return 1;
    }

    /**
     * Gera id da partida
     * @return 
     */
    public int geraIdPartida(){
        this.idPartidas++;
        return idPartidas;
    }
    
    /**
     * Gera id do jogador
     * @return 
     */
    public int geraIdJogador(){
        Random gerador = new Random();
        return gerador.nextInt(this.nrPartidas*10)+gerador.nextInt(this.nrPartidas*10);
         
    }
    
    /**
     * Verifica se o usario ja esta cadastrado.
     * @param nome
     * @return 
     */
    public boolean usuarioCasdastrado(String nome){
        for(int i=0;i<this.nrPartidas;i++){
            if(partidas[i] != null){
                if((partidas[i].getJogador1() != null && partidas[i].getJogador1().getNome().equals(nome)) || (partidas[i].getJogador2() != null && partidas[i].getJogador2().getNome().equals(nome))){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Encontra a partida do jogador
     * @param idJogador
     * @return 
     */
    public int encontraPartida(int idJogador){
        for(int i=0;i<this.nrPartidas;i++){
            if(partidas[i] != null){
                if((partidas[i].getJogador1() != null && partidas[i].getJogador1().getId() == idJogador) || (partidas[i].getJogador2() != null && partidas[i].getJogador2().getId() == idJogador)){
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Encontra uma partida para o jogador
     * @param j
     * @return 
     */
    public boolean encontraPartidaLivre(Jogador  j){
        //Eh o segundo jogador da partida
        for(int i=0;i<this.nrPartidas;i++){
            if(partidas[i] != null){
                if(partidas[i].getJogador2() == null){
                    partidas[i].setJogador2(j);
                    return true;
                }
            }
            
        }
        
        //eh o primeiro jogador da partida
        for(int i=0;i<this.nrPartidas;i++){
            if(partidas[i] == null){
           
                Partida p = new Partida();
                p.setJogador1(j);
                p.setId(geraIdPartida());
                p.setTempoAguardaJogador(System.currentTimeMillis());
                partidas[i] = p;
                return true;
            }
        }
        return false;
    }
    
    
    public int colocaNaPartida(Jogador  jogador, int partida, int posicao){
        
        if(partidas[partida] == null){ //Se nao existe essa partida
            Partida p = new Partida();
            partidas[partida] = p;
            if(posicao == 1){
                partidas[partida].setJogador1(jogador);System.out.println("C Reg na pos 1 id"+jogador.getId());
                partidas[partida].setVez(1);
            }else{
                partidas[partida].setJogador2(jogador);System.out.println("C reg na pos 2 id"+jogador.getId());
                partidas[partida].setVez(2);
            }
        }else{ // Se ja existe partida
            if(posicao == 1){
                if(partidas[partida].getJogador1() == null){
                    partidas[partida].setJogador1(jogador);System.out.println("reg na pos 1 id"+jogador.getId());
                }else{
                    return -2;
                }
            }else{
                if(partidas[partida].getJogador2() == null){
                    partidas[partida].setJogador2(jogador);System.out.println("reg na pos 2 id"+jogador.getId());
                }else{
                    return -2;
                }
            }
            
            
        }
        
        return jogador.getId();
    }
    
    
    /**
     * Identifica se o jogadar eh o 1 ou o 2
     * @param partida
     * @param idJogador
     * @return 
     */
    public int identificaJogador(int partida, int idJogador){
        if(this.partidas[partida].getJogador1() != null){
            if(this.partidas[partida].getJogador1().getId() == idJogador)
                return 1;
        }
        if(this.partidas[partida].getJogador2() != null){
            if(this.partidas[partida].getJogador2().getId() == idJogador)
                return 2;
        }
        return 0;
    }
    
    public int descobreCor(int carta){
        if(carta >=0 && carta <=24){ //Azul
            return 0;
        }
        if(carta >=25 && carta <=49){//Amarela 
            return 1;
        }
        if(carta >=50 && carta <=74){//Verde
            return 2;
        }
        if(carta >=75 && carta <=99){//Vermelha
            return 3;
        }
        return 0;
    }

    
    
    /**
     * Dicionario de cartas
     * @param carta
     * @return 
     */
    public String dicionarioCartas(int carta) {

        if (carta < 0 || carta > 107) {
            return null;
        }

        switch (carta) {
            case 0:
                return "0/Az";
            case 1:
                return "1/Az";
            case 2:
                return "1/Az";
            case 3:
                return "2/Az";
            case 4:
                return "2/Az";
            case 5:
                return "3/Az";
            case 6:
                return "3/Az";
            case 7:
                return "4/Az";
            case 8:
                return "4/Az";
            case 9:
                return "5/Az";
            case 10:
                return "5/Az";
            case 11:
                return "6/Az";
            case 12:
                return "6/Az";
            case 13:
                return "7/Az";
            case 14:
                return "7/Az";
            case 15:
                return "8/Az";
            case 16:
                return "8/Az";
            case 17:
                return "9/Az";
            case 18:
                return "9/Az";
            case 19:
                return "Pu/Az";
            case 20:
                return "Pu/Az";
            case 21:
                return "In/Az";
            case 22:
                return "In/Az";
            case 23:
                return "+2/Az";
            case 24:
                return "+2/Az";
            case 25:
                return "0/Am";
            case 26:
                return "1/Am";
            case 27:
                return "1/Am";
            case 28:
                return "2/Am";
            case 29:
                return "2/Am";
            case 30:
                return "3/Am";
            case 31:
                return "3/Am";
            case 32:
                return "4/Am";
            case 33:
                return "4/Am";
            case 34:
                return "5/Am";
            case 35:
                return "5/Am";
            case 36:
                return "6/Am";
            case 37:
                return "6/Am";
            case 38:
                return "7/Am";
            case 39:
                return "7/Am";
            case 40:
                return "8/Am";
            case 41:
                return "8/Am";
            case 42:
                return "9/Am";
            case 43:
                return "9/Am";
            case 44:
                return "Pu/Am";
            case 45:
                return "Pu/Am";
            case 46:
                return "In/Am";
            case 47:
                return "In/Am";
            case 48:
                return "+2/Am";
            case 49:
                return "+2/Am";
            case 50:
                return "0/Vd";
            case 51:
                return "1/Vd";
            case 52:
                return "1/Vd";
            case 53:
                return "2/Vd";
            case 54:
                return "2/Vd";
            case 55:
                return "3/Vd";
            case 56:
                return "3/Vd";
            case 57:
                return "4/Vd";
            case 58:
                return "4/Vd";
            case 59:
                return "5/Vd";
            case 60:
                return "5/Vd";
            case 61:
                return "6/Vd";
            case 62:
                return "6/Vd";
            case 63:
                return "7/Vd";
            case 64:
                return "7/Vd";
            case 65:
                return "8/Vd";
            case 66:
                return "8/Vd";
            case 67:
                return "9/Vd";
            case 68:
                return "9/Vd";
            case 69:
                return "Pu/Vd";
            case 70:
                return "Pu/Vd";
            case 71:
                return "In/Vd";
            case 72:
                return "In/Vd";
            case 73:
                return "+2/Vd";
            case 74:
                return "+2/Vd";
            case 75:
                return "0/Vm";
            case 76:
                return "1/Vm";
            case 77:
                return "1/Vm";
            case 78:
                return "2/Vm";
            case 79:
                return "2/Vm";
            case 80:
                return "3/Vm";
            case 81:
                return "3/Vm";
            case 82:
                return "4/Vm";
            case 83:
                return "4/Vm";
            case 84:
                return "5/Vm";
            case 85:
                return "5/Vm";
            case 86:
                return "6/Vm";
            case 87:
                return "6/Vm";
            case 88:
                return "7/Vm";
            case 89:
                return "7/Vm";
            case 90:
                return "8/Vm";
            case 91:
                return "8/Vm";
            case 92:
                return "9/Vm";
            case 93:
                return "9/Vm";
            case 94:
                return "Pu/Vm";
            case 95:
                return "Pu/Vm";
            case 96:
                return "In/Vm";
            case 97:
                return "In/Vm";
            case 98:
                return "+2/Vm";
            case 99:
                return "+2/Vm";
            case 100:
                return "Cg/*";
            case 101:
                return "Cg/*";
            case 102:
                return "Cg/*";
            case 103:
                return "Cg/*";
            case 104:
                return "C4/*";
            case 105:
                return "C4/*";
            case 106:
                return "C4/*";
            case 107:
                return "C4/*";

        }

        return "";
    }
    
}
