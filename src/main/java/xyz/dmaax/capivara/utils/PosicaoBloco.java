package xyz.dmaax.capivara.utils;

import java.io.Serializable;

public class PosicaoBloco implements Serializable {
    public int x, y, z;

    public PosicaoBloco(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object outroObjeto) {
        if (this == outroObjeto)
            return true;
        if (outroObjeto == null || getClass() != outroObjeto.getClass())
            return false;
        PosicaoBloco outraPosicao = (PosicaoBloco) outroObjeto;
        return x == outraPosicao.x && y == outraPosicao.y && z == outraPosicao.z;
    }

    @Override
    public int hashCode() {
        int valorHash = x;
        valorHash = 20 * valorHash + y;
        valorHash = 20 * valorHash + z;
        return valorHash;
    }
}