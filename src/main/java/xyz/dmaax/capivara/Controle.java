package xyz.dmaax.capivara;

import xyz.dmaax.capivara.utils.ResultadoLancamentoRaio;

import static org.lwjgl.glfw.GLFW.*;

public class Controle {
    // Variáveis para controle do mouse
    private double mouseAnteriorX, mouseAnteriorY;
    private boolean primeiroEventoMouse = true;
    private final Camera camera;
    private final Mundo mundo;
    private final Janela janela;

    public Controle(Janela janela, Camera camera, Mundo mundo) {
        this.janela = janela;
        this.camera = camera;
        this.mundo = mundo;
    }

    public void registrarCallbacks() {
        long handle = janela.getHandle();
        // esta é uma func anonima q vai ser rodada toda vez q uma tecla for apertada, nesse caso
        //janela, a tecla, scancode é o codigo fisico da tecla, normalmente as pessoas iguinoram ela, só é usada e casos mt específicos
        // o action é oq acontece, tipo quando clicar, nesse caso, é release, ent tem q clicar e soltar, só dai vai fazer, ex GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
        glfwSetKeyCallback(handle, (janelaEvt, tecla, codigoFisicoTecla, acaoTecla, modificadoresTecla) -> {
            if (tecla == GLFW_KEY_ESCAPE && acaoTecla == GLFW_RELEASE)
                // dai ele vai fechar a janela
                glfwSetWindowShouldClose(janelaEvt, true);

            if (tecla == GLFW_KEY_P && acaoTecla == GLFW_RELEASE) {
                mundo.salvarMundo();
            }
        });

        // Callback para detectar o movimento do mouse e atualizar a orientação da câmera
        glfwSetCursorPosCallback(handle, (janelaEvt, posX, posY) -> {
            if (primeiroEventoMouse) {
                mouseAnteriorX = posX;
                mouseAnteriorY = posY;
                primeiroEventoMouse = false;
            }
            double deslocMouseX = posX - mouseAnteriorX;
            double deslocMouseY = posY - mouseAnteriorY ;
            mouseAnteriorX = posX;
            mouseAnteriorY = posY;
            float sensibilidadeMouse = 0.1f;
            camera.rotacionar(deslocMouseX * sensibilidadeMouse, deslocMouseY * sensibilidadeMouse);
        });

        // Esconde e captura o cursor para usar o mouse no controle da câmera
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Callback para os botões do mouse:
        // Botao esquerdo para quebrar blocos
        // Botao direito para colocar blocos
        glfwSetMouseButtonCallback(handle, (janelaEvt, botaoEvt, acaoMouse, modsMouse) -> {
            // se é só precionar
            if (acaoMouse == GLFW_PRESS) {
                ResultadoLancamentoRaio resultadoDoLancamento = mundo.performRaycast(camera);

                if (botaoEvt == GLFW_MOUSE_BUTTON_LEFT) { // Quebra bloco
                    if (resultadoDoLancamento.atingiuAlvo) {
                        mundo.removerBloco(resultadoDoLancamento.xAlvo, resultadoDoLancamento.yAlvo, resultadoDoLancamento.zAlvo);
                    }
                } else if (botaoEvt == GLFW_MOUSE_BUTTON_RIGHT) { // Coloca bloco
                    if (resultadoDoLancamento.atingiuAlvo) {
                        mundo.adicionarBloco(resultadoDoLancamento.xBlocoAdjacente, resultadoDoLancamento.yBlocoAdjacente, resultadoDoLancamento.zBlocoAdjacente);
                    }
                }
            }
        });
    }

    public void processarTeclado() {
        float velocidadeMovimentacao = 0.1f;
        //yaw direita e esquerda
        //movimentohorizontal é o num q indica onde esta olhando em graus, e transforma isso em radianos, pq precisa

        //indicam em qual direção está a frente da câmera sem considerar pra cima ou pra baixo, só no chão mesmo
        // por ex, move o mouse para direita o yaw aumenta, para esquerda ele diminui
        //unica diferenca é q transforma isso em radianos para o sen e cos conseguir fz os calculos
        // ex chat gpt ->
        //
        //  90 graus = π / 2 radianos
        //  180 graus = π radianos
        //
        //
        float yawEmRadianos = (float) Math.toRadians(camera.getMovimentoHorizontal());

        // a gente precisa saber para onde o ser esta olhando, assim a gente forma tipo um caminho na matriz
        /*

       z
       |
       |       /
       |     /
       |   /
       | /_________ x

        */
        float frenteX = (float) Math.sin(yawEmRadianos);
        float frenteZ = (float) -Math.cos(yawEmRadianos);

        //isso é igual ao da frente, mas com + 90 graus
        float direitaX = (float) Math.sin(yawEmRadianos + Math.PI / 2.0); // + Math.PI / 2.0 = 90 graus a maixx dai eu ando no lado
        float direitaZ = (float) -Math.cos(yawEmRadianos + Math.PI / 2.0);

        float dx = 0, dy = 0, dz = 0;

        if (glfwGetKey(janela.getHandle(), GLFW_KEY_W) == GLFW_PRESS) {
            dx += frenteX * velocidadeMovimentacao;
            dz += frenteZ * velocidadeMovimentacao;
        }
        if (glfwGetKey(janela.getHandle(), GLFW_KEY_S) == GLFW_PRESS) {
            dx -= frenteX * velocidadeMovimentacao;
            dz -= frenteZ * velocidadeMovimentacao;
        }

        if (glfwGetKey(janela.getHandle(), GLFW_KEY_A) == GLFW_PRESS) {
            dx -= direitaX * velocidadeMovimentacao;
            dz -= direitaZ * velocidadeMovimentacao;
        }

        if (glfwGetKey(janela.getHandle(), GLFW_KEY_D) == GLFW_PRESS) {
            dx += direitaX * velocidadeMovimentacao;
            dz += direitaZ * velocidadeMovimentacao;
        }

        if (glfwGetKey(janela.getHandle(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            dy += velocidadeMovimentacao;
        }
        if (glfwGetKey(janela.getHandle(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            dy -= velocidadeMovimentacao;
        }

        if (dx != 0 || dy != 0 || dz != 0) {
            camera.mover(dx, dy, dz);
        }
    }
}
