package xyz.dmaax.capivara;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import xyz.dmaax.capivara.exceptions.ExcecaoLeituraDimensoes;
import xyz.dmaax.capivara.utils.PosicaoBloco;
import xyz.dmaax.capivara.utils.Salvar;
import xyz.dmaax.capivara.utils.SalvarImp;

import java.nio.IntBuffer;
import java.util.HashSet;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.*;

class LeitorDimensoes {
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

public class Main extends LeitorDimensoes {

    // é um long pq na vdd ele é um ponteiro, ja q a biblioteca é escrita em c na vdd, ent nesse caso, é um numero de 64 bits, q é o endereco de mm para a janela
    private long window;
    private float cameraX = 0f;
    private float cameraY = 2.0f;  // Um pouco acima do chão
    private float cameraZ = 5f;
    private float movimentohorizontal = 0f;
    private float movimentovertical = 0f;
    // Variáveis para controle do mouse
    private double mouseAnteriorX, mouseAnteriorY;
    private boolean primeiroEventoMouse = true;
    // hashset é para gurdar elementos unicos, sem repeticao, se fosse arraylist teria a possibilidade de ter dois blocos no msm lugar
    // quando usa a func contains(), para saber se ja tem bloco naquela posicao, o tempo q demora é contante, ou seja, se for o primeiro bloco do array ou o ultimo,
    // demora o msm tempo para saber se ele exist
    private HashSet<PosicaoBloco> posicoesDosBlocos = new HashSet<>();


    public HashSet<PosicaoBloco> getPosicoesDosBlocos(){
        return posicoesDosBlocos;
    }


    public void run() {
        init();
        loop();

        // Libera callbacks e destrói a janela
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    //https://github.com/vaaako/Vakraft-Java
    private void init() {
        // a gente tem q chamar essa func q ela vai iniciar a janela
        if (!glfwInit()) {
            throw new IllegalStateException("N foi possivel inicializar o GLFW");
        }
        // essa func reseta tds os valores padroes de janela, o recomendado, uma lista interda do glfw
        // reseta por boa pratica, tipo zerar um registrador antes de fazer uma conta
        glfwDefaultWindowHints();
        // inicia a janela inv para q n de bugs, pisque, ele espera iniciar td e dps coloca ela visível
        // a gente colocou a visibilidade em false
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        //essa serve para o usuario conseguir mudar o tamanho
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        // primiero null é se a janela vai ser fullscreen, mas como a gente quer ler pelo txt, deixei null, o segundo é
        // alguma coisa de janela compartilhada, n entendi direito oq é, mas é para compartilhar contexttos OpenGL, mais ou menos isso, n sei

        try {learquivo();} catch (ExcecaoLeituraDimensoes excecaoLeituraConfig) {System.out.println(excecaoLeituraConfig.getMessage());} // em uma linha msm, da nada

        window = glfwCreateWindow(getLargura(), getAltura(), "Prof Pedro ", NULL, NULL);
        // aq verifica se o ponteiro n é nulo
        if (window == NULL) {
            throw new RuntimeException("Falha ao criar a janela");
        }

        // esta é uma func anonima q vai ser rodada toda vez q uma tecla for apertada, nesse caso
        //janela, a tecla, scancode é o codigo fisico da tecla, normalmente as pessoas iguinoram ela, só é usada e casos mt específicos
        // o action é oq acontece, tipo quando clicar, nesse caso, é release, ent tem q clicar e soltar, só dai vai fazer, ex GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT
        glfwSetKeyCallback(window, (janelaEvt, tecla, codigoFisicoTecla, acaoTecla, modificadoresTecla) -> {
            if (tecla == GLFW_KEY_ESCAPE && acaoTecla == GLFW_RELEASE)
                // dai ele vai fechar a janela
                glfwSetWindowShouldClose(janelaEvt, true);


            if (tecla == GLFW_KEY_P && acaoTecla == GLFW_RELEASE) {
                Salvar salvar = new SalvarImp();
                salvar.salvaMundo(posicoesDosBlocos);
            }
        });


        // Callback para detectar o movimento do mouse e atualizar a orientação da câmera
        glfwSetCursorPosCallback(window, (janelaEvt, posX, posY) -> {
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
            movimentohorizontal += deslocMouseX * sensibilidadeMouse;
            movimentovertical += deslocMouseY * sensibilidadeMouse;
            if (movimentovertical > 90.0f) // evita quebrara  cbc
                movimentovertical = 90.0f;
            if (movimentovertical < -90.0f)
                movimentovertical = -90.0f;
        });

        // Esconde e captura o cursor para usar o mouse no controle da câmera
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Callback para os botões do mouse:
        // Botao esquerdo para quebrar blocos
        // Botao direito para colocar blocos
        glfwSetMouseButtonCallback(window, (janelaEvt, botaoEvt, acaoMouse, modsMouse) -> {
            // se é só precionar
            if (acaoMouse == GLFW_PRESS) {
                ResultadoLancamentoRaio resultadoDoLancamento = performRaycast();

                if (botaoEvt == GLFW_MOUSE_BUTTON_LEFT) { // Quebra bloco
                    if (resultadoDoLancamento.atingiuAlvo) {
                        PosicaoBloco posicaoBlocoRemover = new PosicaoBloco(resultadoDoLancamento.xAlvo, resultadoDoLancamento.yAlvo, resultadoDoLancamento.zAlvo);
                        posicoesDosBlocos.remove(posicaoBlocoRemover);
                    }
                } else if (botaoEvt == GLFW_MOUSE_BUTTON_RIGHT) { // Coloca bloco
                    if (resultadoDoLancamento.atingiuAlvo) {
                        PosicaoBloco posicaoNovoBloco = new PosicaoBloco(resultadoDoLancamento.xBlocoAdjacente, resultadoDoLancamento.yBlocoAdjacente, resultadoDoLancamento.zBlocoAdjacente);
                        // verifica se não ha bloco ja na posicao
                        if (!posicoesDosBlocos.contains(posicaoNovoBloco)) {
                            posicoesDosBlocos.add(posicaoNovoBloco);
                        }
                    }
                }
            }
        });

        // centraliza a janela na tela
        // https://stackoverflow.com/questions/45555227/lwjgl-newbie-what-are-stackpush-mallocint-1-and-similar-glfw-methods
        // aloca memoria de forma segura durante o try, no fim [e dropado
        try (MemoryStack pilha = stackPush()) {
            // nessas duas linhas estamos fazendo uma reserva de espaco na memoria para colocar um int, q vai ser a largura e a altura da janela
            IntBuffer larguraJanelaPtr  = pilha.mallocInt(1);
            IntBuffer alturaJanelaPtr = pilha.mallocInt(1);

            // ag passsamos o real tamanho da janela, n podemos passar int normal pq a biblioteca espera ponteiros
            glfwGetWindowSize(window, larguraJanelaPtr, alturaJanelaPtr);
            // captura info do monitor do ser, como hz, tamanho e coisas assim
            GLFWVidMode modoVideoMonitor = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // resumindo, calcula com base dos pixeis e o tamanho de tela q eu quero para ficar centralizado
            glfwSetWindowPos(
                    window,
                    (modoVideoMonitor.width() - larguraJanelaPtr.get(0)) / 2,
                    (modoVideoMonitor.height() - alturaJanelaPtr.get(0)) / 2
            );
        }
        // pessa o contexto para o opengl, ele precisa saber qq queremos q ele faça ai a gente "conta" para ele
        glfwMakeContextCurrent(window);

        // ativa v-sync
        glfwSwapInterval(1);

        // ag mostra, la no init q eu me lembre a gente colocou false ela para n conseguir ver ela
        glfwShowWindow(window);

        // inicializa os blocos do mundo (o chão)
        initBlocks();
    }

    // aq tem q mexer na persis de objetos, a gente faz um controle de fluxo q tipo se existe o arquivo x na pasta y, le, se n tiver nada, a gente cria e tal, dai tb tem q
    // ter uma opcao de salvar antes de sair, tipo aaa clica a tecla Z e ele vai criar esse arquivo

    private void initBlocks() {
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

    private void loop() {
        // parametro final para fazer funcionar
        GL.createCapabilities();

        // aq garante a profundidade para quando uma face dele estiver na frente da outra ele descarta, assim so mostra a primeira, assim n paraece q estamos com visao de spec por ex
        glEnable(GL_DEPTH_TEST);
        //aq é a regra disso, q vaai ser desenhado só o da frente
        glDepthFunc(GL_LEQUAL);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            // verifica se alguma tecla como w a s d esc foi pressionada e atualiza as var da camera
            processInput();

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


            // mover o mundo é msm coisa q mover a camera, ent faz aq
            glRotatef(movimentovertical, 1.0f, 0.0f, 0.0f);
            glRotatef(movimentohorizontal, 0.0f, 1.0f, 0.0f);
            glTranslatef(-cameraX, -cameraY, -cameraZ);


            // limpa tudo par a px cena
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // desenha todos os blocos do mundo
            for (PosicaoBloco posicaoAtualBloco : posicoesDosBlocos) {
                drawCube(posicaoAtualBloco.x, posicaoAtualBloco.y, posicaoAtualBloco.z);
            }


            glfwSwapBuffers(window);
            // processa eventos do mouse e teclado dnv
            glfwPollEvents();
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

    private void processInput() {
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
        float yawEmRadianos = (float) Math.toRadians(movimentohorizontal);


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


        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            cameraX += frenteX * velocidadeMovimentacao;
            cameraZ += frenteZ * velocidadeMovimentacao;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            cameraX -= frenteX * velocidadeMovimentacao;
            cameraZ -= frenteZ * velocidadeMovimentacao;
        }

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraX -= direitaX * velocidadeMovimentacao;
            cameraZ -= direitaZ * velocidadeMovimentacao;
        }


        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraX += direitaX * velocidadeMovimentacao;
            cameraZ += direitaZ * velocidadeMovimentacao;
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cameraY += velocidadeMovimentacao;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cameraY -= velocidadeMovimentacao;
        }



    }

    // trasnforma uma cordenada do jg q é float para cordenada q bloco q é int, 0.5 para arredondar
    private int worldToBlockCoord(float coordenadaDoMundo) {
        return (int) Math.floor(coordenadaDoMundo + 0.5f);
    }


    private ResultadoLancamentoRaio performRaycast() {
        float distanciaMaxRaio = 6.0f;
        float passoDoRaio = 0.01f;


        // dnv transforma a visao da camera de graus para radianos pq o java n consegue usar graus direito
        float pitchEmRadianos = (float) Math.toRadians(movimentovertical);
        float yawEmRadianos = (float) Math.toRadians(movimentohorizontal);


        // aq a gente cria o vetor 3d q aponta em direcao onde a gente mira
        //se pitch negativo = olhando pra cima dirRaioY positivo
        //se pitch positivo = olhando pra baixo dirRaioY negativo
        float dirRaioX = (float) (Math.cos(pitchEmRadianos) * Math.sin(yawEmRadianos));
        float dirRaioY = (float) -Math.sin(pitchEmRadianos);
        float dirRaioZ = (float) (Math.cos(pitchEmRadianos) * -Math.cos(yawEmRadianos));


        ResultadoLancamentoRaio resultadoBusca = new ResultadoLancamentoRaio();



        int blocoAnteriorX = worldToBlockCoord(cameraX);
        int blocoAnteriorY = worldToBlockCoord(cameraY);
        int blocoAnteriorZ = worldToBlockCoord(cameraZ);

        // aq ele "anda" o vetor 0.1 por ciclo do for
        // anda e radianos e dai transforma em blocos
        for (float distPercorridaRaio = passoDoRaio; distPercorridaRaio <= distanciaMaxRaio; distPercorridaRaio += passoDoRaio) {
            float pontoMundoX = cameraX + dirRaioX * distPercorridaRaio;
            float pontoMundoY = cameraY + dirRaioY * distPercorridaRaio;
            float pontoMundoZ = cameraZ + dirRaioZ * distPercorridaRaio;

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

    // armazena os dados do raycast
    private static class ResultadoLancamentoRaio {
        public boolean atingiuAlvo;
        // coordenadas do bloco atingido
        public int xAlvo;
        public int yAlvo;
        public int zAlvo;
        // coordenadas do bloco anterioro (onde vai colocar o novo bloco)
        public int xBlocoAdjacente;
        public int yBlocoAdjacente;
        public int zBlocoAdjacente;
    }

    //public static void main(String[] argumentosExecucao) {
    //    new Main().run();
    //}
}