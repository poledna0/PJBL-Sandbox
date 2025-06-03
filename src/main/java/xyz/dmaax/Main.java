package xyz.dmaax;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashSet;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import javax.swing.*;
import java.io.*;

class LeTxt{
    int largura = 0;
    int altura = 0;
    public void learquivo(){
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/caboclo/duck/git/PJBL-Sandbox/src/main/java/xyz/dmaax/dimensoes.txt"))) {
            this.largura = Integer.parseInt(reader.readLine());
            this.altura = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
    public int getLargura(){
        return this.largura;
    }

    public int getAltura() {
        return altura;
    }
}


class Tudo extends LeTxt{
    private long window;
    private float cameraX = 0f;
    private float cameraY = 2.0f;  // Um pouco acima do chão
    private float cameraZ = 5f;
    private float yaw = 0f;   // Rotação horizontal
    private float pitch = 0f; // Rotação vertical
    // Variáveis para controle do mouse
    private double lastMouseX, lastMouseY;
    private boolean firstMouse = true;
    //private HashSet<BlockPos> blocks = new HashSet<>();
    private void init(){
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

        learquivo();
        System.out.println(getAltura());
        System.out.println(getLargura());
        window = glfwCreateWindow(largura, altura, "Minecraft aula JAVA", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Falha ao criar a janela");
        }
        // esta é uma func anonima q vai ser rodada toda vez q uma tecla for apertada, nesse caso
        //janela, a tecla, scancode é o codigo fisico da tecla, normalmente as pessoas iguinoram ela, só é usada e casos mt específicos
        // o action é oq acontece, tipo quando clicar, nesse caso, é release, ent tem q clicar e soltar, só dai vai fazer, ex GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                // dai ele vai fechar a janela
                glfwSetWindowShouldClose(window, true);
        });
        // essa é outra func anonima q vai ser chamada td vez q o hamister se mover
        // recebe de parametro a janela, a x e o y do hamister
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            // para na primeira vez q mover a camera n ter um grande salto, quando iniciar ele vai pegar a cordenada anteriror
            // assim n tem um puta movimento no inicio, n sei explicar, se quiser ver tira esse if e testa
            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
            }
            /*
            esssas duas linhas
            double xoffset = xpos - lastMouseX;
            double yoffset = lastMouseY - ypos;

            o xpos é a possicao atual do mouse, a gente subtrai com as cordenadas anterorios q sao a lastMouse
            é inverdido o do y por causa q y = movemos para baixo para subir, ent a gente inverte

            ex do chatgpt (

            xoffset = 110 - 100 = 10 - o mouse andou 10 pixels para a direita.

            yoffset = 200 - 195 = 5 - o mouse andou 5 pixels para cima.

            )

             */
            double xoffset = xpos - lastMouseX;
            double yoffset = lastMouseY - ypos;

            // att as posicoes q ag essa sao as antigas
            lastMouseX = xpos;
            lastMouseY = ypos;

            float sensitivity = 0.1f;
            // yaw = X
            // pitch = Y
            yaw += xoffset * sensitivity;
            pitch += yoffset * sensitivity;

            // para evitar q o boneco quebra a cbc kkkkk
            if (pitch > 90)
                pitch = 90;
            if (pitch < -90)
                pitch = -90;
        });
        // esconde o ponteiro do mouse e trava ele no centro da tela
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // func anonima pera pega
    }
    public void run(){
        init();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }



    public static void main(String[] args) {
        try {
            Tudo tudo = new Tudo();
            tudo.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}