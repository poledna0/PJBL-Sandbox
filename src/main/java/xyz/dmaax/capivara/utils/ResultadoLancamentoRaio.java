package xyz.dmaax.capivara.utils;

/**
 * Armazena os dados do resultado de um raycast
 */
public class ResultadoLancamentoRaio {
    public boolean atingiuAlvo;

    // Coordenadas do bloco atingido
    public int xAlvo;
    public int yAlvo;
    public int zAlvo;

    // Coordenadas do bloco anterior (onde vai colocar o novo bloco)
    public int xBlocoAdjacente;
    public int yBlocoAdjacente;
    public int zBlocoAdjacente;

    // Constructor padrão
    public ResultadoLancamentoRaio() {
        this.atingiuAlvo = false;
    }

    // Constructor com parâmetros para quando atingiu alvo
    public ResultadoLancamentoRaio(boolean atingiuAlvo, int xAlvo, int yAlvo, int zAlvo,
                                   int xBlocoAdjacente, int yBlocoAdjacente, int zBlocoAdjacente) {
        this.atingiuAlvo = atingiuAlvo;
        this.xAlvo = xAlvo;
        this.yAlvo = yAlvo;
        this.zAlvo = zAlvo;
        this.xBlocoAdjacente = xBlocoAdjacente;
        this.yBlocoAdjacente = yBlocoAdjacente;
        this.zBlocoAdjacente = zBlocoAdjacente;
    }

    // Métodos auxiliares
    public void setAlvo(int x, int y, int z) {
        this.atingiuAlvo = true;
        this.xAlvo = x;
        this.yAlvo = y;
        this.zAlvo = z;
    }

    public void setBlocoAdjacente(int x, int y, int z) {
        this.xBlocoAdjacente = x;
        this.yBlocoAdjacente = y;
        this.zBlocoAdjacente = z;
    }

    @Override
    public String toString() {
        if (atingiuAlvo) {
            return String.format("Raycast hit at (%d, %d, %d), adjacent block at (%d, %d, %d)",
                    xAlvo, yAlvo, zAlvo, xBlocoAdjacente, yBlocoAdjacente, zBlocoAdjacente);
        } else {
            return "Raycast missed";
        }
    }
}