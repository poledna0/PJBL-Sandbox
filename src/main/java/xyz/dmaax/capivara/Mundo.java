package xyz.dmaax.capivara;

import xyz.dmaax.capivara.utils.PosicaoBloco;
import xyz.dmaax.capivara.utils.ResultadoLancamentoRaio;
import xyz.dmaax.capivara.utils.Salvar;
import xyz.dmaax.capivara.utils.SalvarImp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;

public class Mundo {
    // hashset é para gurdar elementos unicos, sem repeticao, se fosse arraylist teria a possibilidade de ter dois blocos no msm lugar
    // quando usa a func contains(), para saber se ja tem bloco naquela posicao, o tempo q demora é contante, ou seja, se for o primeiro bloco do array ou o ultimo,
    // demora o msm tempo para saber se ele exist
    private HashSet<PosicaoBloco> posicoesDosBlocos = new HashSet<>();

    // aq tem q mexer na persis de objetos, a gente faz um controle de fluxo q tipo se existe o arquivo x na pasta y, le, se n tiver nada, a gente cria e tal, dai tb tem q
    // ter uma opcao de salvar antes de sair, tipo aaa clica a tecla Z e ele vai criar esse arquivo
    public void carregarOuGerarInicial() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/main/java/xyz/dmaax/capivara/blocos.dat"))) {
            posicoesDosBlocos = (HashSet<PosicaoBloco>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            posicoesDosBlocos = new HashSet<>();
            for (int x = -15; x < 15; x++) {
                for (int z = -15; z < 15; z++) {
                    posicoesDosBlocos.add(new PosicaoBloco(x, 0, z));
                    posicoesDosBlocos.add(new PosicaoBloco(x, -1, z));
                }
            }
        }
    }

    public void salvarMundo() {
        Salvar salvar = new SalvarImp();
        salvar.salvaMundo(posicoesDosBlocos);
    }

    public void adicionarBloco(int x, int y, int z) {
        PosicaoBloco posicaoNovoBloco = new PosicaoBloco(x, y, z);
        // verifica se não ha bloco ja na posicao
        if (!posicoesDosBlocos.contains(posicaoNovoBloco)) {
            posicoesDosBlocos.add(posicaoNovoBloco);
        }
    }

    public void removerBloco(int x, int y, int z) {
        PosicaoBloco posicaoBlocoRemover = new PosicaoBloco(x, y, z);
        posicoesDosBlocos.remove(posicaoBlocoRemover);
    }

    public ResultadoLancamentoRaio performRaycast(Camera camera) {
        float distanciaMaxRaio = 6.0f;
        float passoDoRaio = 0.01f;

        // dnv transforma a visao da camera de graus para radianos pq o java n consegue usar graus direito
        float pitchEmRadianos = (float) Math.toRadians(camera.getMovimentoVertical());
        float yawEmRadianos = (float) Math.toRadians(camera.getMovimentoHorizontal());

        // aq a gente cria o vetor 3d q aponta em direcao onde a gente mira
        //se pitch negativo = olhando pra cima dirRaioY positivo
        //se pitch positivo = olhando pra baixo dirRaioY negativo
        float dirRaioX = (float) (Math.cos(pitchEmRadianos) * Math.sin(yawEmRadianos));
        float dirRaioY = (float) -Math.sin(pitchEmRadianos);
        float dirRaioZ = (float) (Math.cos(pitchEmRadianos) * -Math.cos(yawEmRadianos));

        ResultadoLancamentoRaio resultadoBusca = new ResultadoLancamentoRaio();

        int blocoAnteriorX = worldToBlockCoord(camera.getCameraX());
        int blocoAnteriorY = worldToBlockCoord(camera.getCameraY());
        int blocoAnteriorZ = worldToBlockCoord(camera.getCameraZ());

        // aq ele "anda" o vetor 0.1 por ciclo do for
        // anda e radianos e dai transforma em blocos
        for (float distPercorridaRaio = passoDoRaio; distPercorridaRaio <= distanciaMaxRaio; distPercorridaRaio += passoDoRaio) {
            float pontoMundoX = camera.getCameraX() + dirRaioX * distPercorridaRaio;
            float pontoMundoY = camera.getCameraY() + dirRaioY * distPercorridaRaio;
            float pontoMundoZ = camera.getCameraZ() + dirRaioZ * distPercorridaRaio;

            int candidatoX = worldToBlockCoord(pontoMundoX);
            int candidatoY = worldToBlockCoord(pontoMundoY);
            int candidatoZ = worldToBlockCoord(pontoMundoZ);

            if (posicoesDosBlocos.contains(new PosicaoBloco(candidatoX, candidatoY, candidatoZ))) {
                resultadoBusca.atingiuAlvo = true;
                resultadoBusca.xAlvo = candidatoX;
                resultadoBusca.yAlvo = candidatoY;
                resultadoBusca.zAlvo = candidatoZ;

                resultadoBusca.xBlocoAdjacente = blocoAnteriorX;
                resultadoBusca.yBlocoAdjacente = blocoAnteriorY;
                resultadoBusca.zBlocoAdjacente = blocoAnteriorZ;
                return resultadoBusca;
            }
            blocoAnteriorX = candidatoX;
            blocoAnteriorY = candidatoY;
            blocoAnteriorZ = candidatoZ;
        }

        resultadoBusca.atingiuAlvo = false;
        return resultadoBusca;
    }

    // trasnforma uma cordenada do jg q é float para cordenada q bloco q é int, 0.5 para arredondar
    private int worldToBlockCoord(float coordenadaDoMundo) {
        return (int) Math.floor(coordenadaDoMundo + 0.5f);
    }

    public HashSet<PosicaoBloco> getPosicoesDosBlocos() {
        return posicoesDosBlocos;
    }
}
