package xyz.dmaax.capivara;

import xyz.dmaax.capivara.exceptions.ExcecaoLeituraDimensoes;
import xyz.dmaax.capivara.utils.LeitorDimensoes;

public class Jogo {

    private Janela janela;
    private Renderizador renderizador;
    private Camera camera;
    private Mundo mundo;
    private Controle controle;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Usa o LeitorDimensoes para obter as configurações
        LeitorDimensoes leitor = new LeitorDimensoes();
        try {
            leitor.learquivo();
        } catch (ExcecaoLeituraDimensoes excecaoLeituraConfig) {
            System.out.println(excecaoLeituraConfig.getMessage());
        } // em uma linha msm, da nada

        // Cria os componentes principais do jogo
        janela = new Janela(leitor.getLargura(), leitor.getAltura());
        camera = new Camera();
        mundo = new Mundo();
        renderizador = new Renderizador();
        controle = new Controle(janela, camera, mundo);

        // Inicializa os componentes
        janela.inicializar();
        renderizador.inicializar();
        // inicializa os blocos do mundo (o chão)
        mundo.carregarOuGerarInicial();
        // Configura os callbacks de input
        controle.registrarCallbacks();
    }

    private void loop() {
        while (!janela.deveFechar()) {
            // verifica se alguma tecla como w a s d esc foi pressionada e atualiza as var da camera
            controle.processarTeclado();
            // Renderiza a cena
            renderizador.renderizarFrame(janela.getHandle(), camera, mundo);
            // Atualiza a janela (troca buffers e processa eventos)
            janela.atualizarFrame();
        }
    }

    private void cleanup() {
        janela.destruir();
    }

    public static void main(String[] args) {
        new Jogo().run();
    }
}