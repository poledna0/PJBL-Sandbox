package xyz.dmaax.capivara.exceptions;

public class ExcecaoLeituraDimensoes extends Exception {
    public ExcecaoLeituraDimensoes(String mensagem) {
        super("Erro de leitura de arquivo " + mensagem);
    }
}
