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
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dimensoes.txt");
             BufferedReader leitor = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new ExcecaoLeituraDimensoes("Arquivo dimensoes.txt não encontrado nos resources");
            }

            this.largura = Integer.parseInt(leitor.readLine());
            this.altura = Integer.parseInt(leitor.readLine());
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