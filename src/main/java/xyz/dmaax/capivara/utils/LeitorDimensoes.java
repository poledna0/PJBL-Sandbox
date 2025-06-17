package xyz.dmaax.capivara.utils;

import xyz.dmaax.capivara.exceptions.ExcecaoLeituraDimensoes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LeitorDimensoes {
    private int largura = 0;
    private int altura = 0;

    public void learquivo() throws ExcecaoLeituraDimensoes {
        InputStream entrada = null;
        BufferedReader leitor = null;

        try {
            entrada = getClass().getClassLoader().getResourceAsStream("dimensoes.txt");

            if (entrada == null) {
                throw new ExcecaoLeituraDimensoes(" erro na leitura");
            }

            leitor = new BufferedReader(new InputStreamReader(entrada));

            this.largura = Integer.parseInt(leitor.readLine());
            this.altura = Integer.parseInt(leitor.readLine());
            //String ex = leitor.readLine();
        } catch (IOException e) {
            throw new ExcecaoLeituraDimensoes(e.getMessage());
        } catch (NumberFormatException e2) {
            throw new ExcecaoLeituraDimensoes(e2.getMessage());
        }
    }


    public int getLargura() {
        return this.largura;
    }

    public int getAltura() {
        return altura;
    }
}