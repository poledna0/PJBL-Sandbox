package xyz.dmaax.capivara;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Janela {

    // é um long pq na vdd ele é um ponteiro, ja q a biblioteca é escrita em c na vdd, ent nesse caso, é um numero de 64 bits, q é o endereco de mm para a janela
    private long handle;
    private int largura;
    private int altura;

    public Janela(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
    }

    public void inicializar() {
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
        handle = glfwCreateWindow(this.largura, this.altura, "Prof Pedro ", NULL, NULL);
        // aq verifica se o ponteiro n é nulo
        if (handle == NULL) {
            throw new RuntimeException("Falha ao criar a janela");
        }

        // centraliza a janela na tela
        // https://stackoverflow.com/questions/45555227/lwjgl-newbie-what-are-stackpush-mallocint-1-and-similar-glfw-methods
        // aloca memoria de forma segura durante o try, no fim [e dropado
        try (MemoryStack pilha = stackPush()) {
            // nessas duas linhas estamos fazendo uma reserva de espaco na memoria para colocar um int, q vai ser a largura e a altura da janela
            IntBuffer larguraJanelaPtr  = pilha.mallocInt(1);
            IntBuffer alturaJanelaPtr = pilha.mallocInt(1);

            // ag passsamos o real tamanho da janela, n podemos passar int normal pq a biblioteca espera ponteiros
            glfwGetWindowSize(handle, larguraJanelaPtr, alturaJanelaPtr);
            // captura info do monitor do ser, como hz, tamanho e coisas assim
            GLFWVidMode modoVideoMonitor = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // resumindo, calcula com base dos pixeis e o tamanho de tela q eu quero para ficar centralizado
            glfwSetWindowPos(
                    handle,
                    (modoVideoMonitor.width() - larguraJanelaPtr.get(0)) / 2,
                    (modoVideoMonitor.height() - alturaJanelaPtr.get(0)) / 2
            );
        }
        // pessa o contexto para o opengl, ele precisa saber qq queremos q ele faça ai a gente "conta" para ele
        glfwMakeContextCurrent(handle);
        // ativa v-sync
        glfwSwapInterval(1);
        // ag mostra, la no init q eu me lembre a gente colocou false ela para n conseguir ver ela
        glfwShowWindow(handle);
    }

    public boolean deveFechar() {
        return glfwWindowShouldClose(handle);
    }

    public void atualizarFrame() {
        glfwSwapBuffers(handle);
        // processa eventos do mouse e teclado dnv
        glfwPollEvents();
    }

    public void destruir() {
        // Libera callbacks e destrói a janela
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public long getHandle() {
        return handle;
    }
}