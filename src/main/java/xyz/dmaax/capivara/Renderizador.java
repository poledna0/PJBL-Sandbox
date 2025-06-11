package xyz.dmaax.capivara;

import org.lwjgl.system.MemoryStack;
import xyz.dmaax.capivara.utils.PosicaoBloco;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Renderizador {

    public void inicializar() {
        // parametro final para fazer funcionar
        org.lwjgl.opengl.GL.createCapabilities();

        // aq garante a profundidade para quando uma face dele estiver na frente da outra ele descarta, assim so mostra a primeira, assim n paraece q estamos com visao de spec por ex
        glEnable(GL_DEPTH_TEST);
        //aq é a regra disso, q vaai ser desenhado só o da frente
        glDepthFunc(GL_LEQUAL);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void renderizarFrame(long window, Camera camera, Mundo mundo) {
        // prepara toda a parte de renderizar a janela, aq diz q vai usar a janela toda
        try (MemoryStack pilha = stackPush()) {
            IntBuffer larguraFbPtr = pilha.mallocInt(1);
            IntBuffer alturaFbPtr = pilha.mallocInt(1);
            glfwGetFramebufferSize(window, larguraFbPtr, alturaFbPtr);
            int larguraFrameBuffer = larguraFbPtr.get(0);
            int alturaFrameBuffer = alturaFbPtr.get(0);
            glViewport(0, 0, larguraFrameBuffer, alturaFrameBuffer);

            // configura a matriz de projeção de 3d para 2d na tela
            glMatrixMode(GL_PROJECTION);
            // limpa a tela antes de imprimir dnv
            glLoadIdentity();

            // razao entre largura e altura da tela
            float proporcaoAspecto = (alturaFrameBuffer == 0) ? 1.0f : (float) larguraFrameBuffer / alturaFrameBuffer;
            // conta maluca para n esticar a img, tipo jogar cs em 4.3 em um monitor de 16.9
            setPerspective(75.0f, proporcaoAspecto, 0.1f, 70.0f);

            //posição da câmera e objetos
            glMatrixMode(GL_MODELVIEW);
            // da clean dnv
            glLoadIdentity();
        }

        // aplica a transformacao da camera
        camera.aplicarTransformacao();

        // limpa tudo par a px cena
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // desenha todos os blocos do mundo
        for (PosicaoBloco posicaoAtualBloco : mundo.getPosicoesDosBlocos()) {
            drawCube(posicaoAtualBloco.x, posicaoAtualBloco.y, posicaoAtualBloco.z);
        }
    }

    //                           fov, quando maior mais coisa ve
    //                                  |
    //                                  |
    //                                  |
    //                                  |
    //                                  |             foi feita la em cima
    //                                  |           o nome da var               esses dois é a distancia minima e mx q precisa estar
    //                                  |         proporcaoAspecto                   para conseguir render
    private void setPerspective(float anguloVisaoY, float taxaAspecto, float planoProximo, float planoDistante) {
        // resumindo essa bencao
        // converde o angulo da visao para radianos, math.tan só trabalha com radianos
        // explicacao do chat ->
        /*
         *Divide por 2 porque é como se fosse a metade do ângulo para cima e para baixo (pensa numa pirâmide com vértice na sua câmera).
         *Usa Math.tan() para calcular o tamanho da face de cima do frustum (forma de pirâmide cortada) na profundidade planoProximo.
         * O resultado é a coordenada do topo da visão 3D naquele plano
         * */
        float topoPerspectiva = (float)Math.tan(Math.toRadians(anguloVisaoY / 2)) * planoProximo;
        // parte de baixo é simétrica à de cima, mas negativa. Isso define o campo de visão vertical
        float basePerspectiva = -topoPerspectiva;
        // tipo pego a parte de cima e o calculo da tela para saber o tamanho do lado
        float direitaPerspectiva = topoPerspectiva * taxaAspecto;
        float esquerdaPerspectiva = -direitaPerspectiva;
        //Monta a matriz de perspectiva (a piramide)
        glFrustum(esquerdaPerspectiva, direitaPerspectiva, basePerspectiva, topoPerspectiva, planoProximo, planoDistante);
    }

    // x, y, z são as coordenadas do centro do cubo.
    private void drawCube(int x, int y, int z) {
        glPushMatrix();

        // a gente vai para uma das pontas, um vertice do bloco
        glTranslatef(x - 0.5f, y - 0.5f, z - 0.5f);

        // prepara a construcao
        glBegin(GL_QUADS);

        // vermelho
        glColor3f(1.0f, 0.0f, 0.0f);

        glVertex3f(0.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(0.0f, 1.0f, 1.0f);

        // verde
        glColor3f(0.0f, 1.0f, 0.0f);

        glVertex3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);
        glVertex3f(1.0f, 1.0f, 0.0f);

        // azul
        glColor3f(0.0f, 0.0f, 1.0f);

        glVertex3f(1.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 1.0f, 0.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);

        // amarelo
        glColor3f(1.0f, 1.0f, 0.0f);

        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 1.0f);
        glVertex3f(0.0f, 1.0f, 1.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);

        // ciano
        glColor3f(0.0f, 1.0f, 1.0f);

        glVertex3f(0.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);

        // rosa
        glColor3f(1.0f, 0.0f, 1.0f);

        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 0.0f, 1.0f);
        glVertex3f(0.0f, 0.0f, 1.0f);

        glEnd();

        glPopMatrix();
    }
}