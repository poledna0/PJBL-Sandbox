import java.util.ArrayList;
import java.util.Random;

enum TipoBloco {
    TRONCO,
    FOLHAS,
    GRAMA,
    PEDRA,
    TERRA
}

class Arvore{
    private ArrayList<TipoBloco> blocos_arvore;
    private int x, z, altura_chao;
    // mine sequencia x, y, z

    public Arvore(int altura_chao, int x, int z ){
        ArrayList <TipoBloco> objarvore = new ArrayList<TipoBloco>();

        Random r = new Random(); // vai criar um y aleatorio para os troncos da arvode

        for (int i = 0; i <= 4 + r.nextInt(3); i ++ ){
            objarvore.add(TipoBloco.TRONCO);
        }

        this.x = x;
        this.z = z;
        this.altura_chao = altura_chao;

        //add folha

    }
}
