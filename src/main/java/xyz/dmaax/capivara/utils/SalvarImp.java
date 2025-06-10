package xyz.dmaax.capivara.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class SalvarImp extends Salvar {
    public void salvaMundo(HashSet<PosicaoBloco> posicoesDosBlocos) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/java/xyz/dmaax/capivara/blocos.dat"))) {
            oos.writeObject(posicoesDosBlocos);
            System.out.println("Mundo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println("Falha ao salvar o mundo.");
            e.printStackTrace();
        }
    }
}
