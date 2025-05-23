# Projeto: Minecraft Simples em Java com LWJGL

Este é um projeto desenvolvido por três integrantes com o objetivo de criar uma versão simples de Minecraft utilizando Java e a biblioteca LWJGL (Lightweight Java Game Library). 

### Integrantes:
- **Henrique Poledna**
- **Eduardo Machado de Oliveira Dluhosch**
- **Camilla Hollmann**

## Descrição

O projeto visa criar uma versão básica de Minecraft com as seguintes funcionalidades:

- **Mundo Super Plano**: O mundo terá três tipos de blocos.
- **Movimentação**: O jogador poderá se mover livremente pelo mundo.
- **Árvores e Vilas**: Serão geradas árvores e, talvez, uma vila no mundo.
- **Inventário**: O jogador terá um inventário simples para interagir com o mundo.

### Hierarquia de Classes Sugerida

```java
// Req. 3 & 4: Classe abstrata com método abstrato
abstract class GameObject {
    protected Vector3f position;
    protected boolean visible;
    
    // Req. 4: Método abstrato
    public abstract void render();
    public abstract void update(float deltaTime);
}

// Req. 6 & 7: Herança e sobrescrita
class Block extends GameObject {
    private BlockType type;
    private boolean solid;
    
    @Override
    public void render() { /* implementação específica */ }
    @Override 
    public void update(float deltaTime) { /* implementação específica */ }
}

class Player extends GameObject {
    private Vector3f velocity;
    private Inventory inventory; // Req. 9: Associação
    private boolean onGround;
    
    @Override
    public void render() { /* implementação específica */ }
    @Override
    public void update(float deltaTime) { /* implementação específica */ }
}

// Req. 11: Exception customizada
class ChunkLoadException extends Exception {
    public ChunkLoadException(String message) { super(message); }
}

// Req. 8: Polimorfismo - diferentes tipos de blocos
interface Renderable {
    void render();
}

class GrassBlock extends Block implements Renderable { }
class StoneBlock extends Block implements Renderable { }
class WoodBlock extends Block implements Renderable { }
```

### Classes Principais (Req. 2: Mínimo 5 classes)

1. **GameObject** (abstrata) - Req. 3 & 4
2. **Block** - herda de GameObject - Req. 6 & 7  
3. **Player** - herda de GameObject - Req. 6 & 7
4. **Chunk** - contém ArrayList<Block> - Req. 10
5. **World** - gerencia chunks e persistência - Req. 13 & 14
6. **Camera** - controle de visualização
7. **Renderer** - sistema de renderização - Req. 12
8. **Inventory** - sistema de inventário
9. **GameEngine** - loop principal do jogo
10. **ChunkLoadException** - exceção customizada - Req. 11

---

## 📅 Cronograma de Desenvolvimento

### Semana 1: Fundação + Estrutura POO

| Pessoa | Especialização | Tarefas |
|--------|----------------|---------|
| **Pessoa 1** | **Engine/Rendering** | • **POO:** Criar classe abstrata `GameObject` com métodos abstratos<br>• **POO:** Implementar classe `Camera` e `Renderer`<br>• Configurar LWJGL + OpenGL + JOML<br>• Sistema básico de janela e input<br>• **Req. 12:** Interface gráfica básica<br>• Shader básico para cubos |
| **Pessoa 2** | **World/Chunks** | • **POO:** Criar classes `World`, `Chunk` e hierarquia `Block`<br>• **POO:** Implementar herança (`GrassBlock`, `StoneBlock` extends `Block`)<br>• **Req. 10:** Usar `ArrayList<Block>` nos chunks<br>• **Req. 9:** Associação `World` -> `Chunk` -> `Block`<br>• Geração básica de terreno<br>• **Req. 11:** Criar `ChunkLoadException` |
| **Pessoa 3** | **Interface/Menus** | • **POO:** Criar classes `Player`, `Inventory`, `GameEngine`<br>• **POO:** `Player` herda de `GameObject`<br>• **Req. 9:** Associação `Player` -> `Inventory`<br>• Menu principal e HUD<br>• Sistema de estados do jogo<br>• **Req. 13:** Preparar leitura de config.txt |

### Semana 2: Mecânicas Core + POO Avançado

| Pessoa | Especialização | Tarefas |
|--------|----------------|---------|
| **Pessoa 1** | **Physics/Collision** | • **Req. 7:** Implementar métodos abstratos `render()` e `update()` em subclasses<br>• **Req. 8:** Usar polimorfismo para renderizar diferentes `GameObject`s<br>• Detecção de colisão jogador-mundo<br>• Física básica (gravidade, pulo)<br>• Raycasting para seleção de blocos |
| **Pessoa 2** | **Block System** | • **POO:** Expandir hierarquia de blocos (mínimo 3 tipos)<br>• **Req. 8:** Polimorfismo na renderização de blocos<br>• Sistema de quebrar/colocar blocos<br>• Meshing otimizado<br>• **Req. 13:** Carregar texturas de arquivo .txt<br>• Texturas básicas |
| **Pessoa 3** | **Game Logic** | • **Req. 14:** Sistema de save/load usando serialização<br>• **POO:** Garantir encapsulamento em todas as classes<br>• **Req. 5:** Verificar se classes têm atributos/métodos suficientes<br>• Inventário funcional<br>• Sistema de seleção de blocos<br>• Integração dos sistemas |

### Semana 3: Polish + Validação POO

| Pessoa | Especialização | Tarefas |
|--------|----------------|---------|
| **Todos** | **Colaboração + POO** | • **Validação:** Verificar todos os 14 requisitos POO<br>• Debug e otimização<br>• **Req. 11:** Implementar tratamento de exceções<br>• **Req. 12:** Polir interface gráfica<br>• Documentação JavaDoc<br>• Preparação da apresentação<br>• **Teste final:** Salvar/carregar mundo completo |

---

## 📋 Mapeamento Detalhado dos Requisitos

### Como Cada Requisito Será Atendido:

| Req. | Descrição | Implementação no Projeto |
|------|-----------|-------------------------|
| **1** | Encapsulamento | Atributos privados com getters/setters em todas as classes |
| **2** | Mínimo 5 classes | GameObject, Block, Player, Chunk, World, Camera, Renderer, etc. |
| **3** | Classe abstrata | `GameObject` como classe base abstrata |
| **4** | Método abstrato | `render()` e `update()` abstratos em GameObject |
| **5** | 10+ atributos/métodos | Distribuídos entre todas as classes principais |
| **6** | Duas heranças | `Block extends GameObject`, `Player extends GameObject` |
| **7** | Método sobrescrito | Implementação de `render()` e `update()` nas subclasses |
| **8** | Polimorfismo | `ArrayList<GameObject>` renderizando diferentes tipos |
| **9** | Associação | `Player` -> `Inventory`, `World` -> `Chunk` -> `Block` |
| **10** | ArrayList | `ArrayList<Block>` em Chunk, `ArrayList<Chunk>` em World |
| **11** | Exception customizada | `ChunkLoadException` para erros de carregamento |
| **12** | Interface gráfica | GUI com LWJGL/OpenGL |
| **13** | Leitura arquivo | Configurações do jogo de `config.txt` |
| **14** | Persistência | Save/load do mundo usando serialização Java |

---

## 🎯 MVP (Produto Mínimo Viável)

### Funcionalidades Essenciais
- [ ] Mundo 3D com blocos básicos renderizados
- [ ] Jogador que anda, pula e colide com blocos
- [ ] Sistema de colocar/quebrar pelo menos 1 tipo de bloco
- [ ] Interface básica (menu principal + HUD simples)
- [ ] Controles funcionais (WASD + mouse)

### Funcionalidades Extras (se houver tempo)
- [ ] Múltiplos tipos de blocos
- [ ] Sistema de iluminação
- [ ] Efeitos sonoros
- [ ] Inventário completo
- [ ] Save/Load do mundo

---

## 🛠️ Estrutura do Projeto

### Pacotes Sugeridos
```
src/
├── engine/          # Rendering, shaders, camera
├── world/           # Chunks, blocos, geração de terreno
├── physics/         # Colisões, movimento
├── gui/             # Interface, menus, HUD
├── game/            # Logic principal, estados
└── utils/           # Utilitários gerais
```

### Classes Principais
- `Game` - Classe principal e game loop
- `Camera` - Controle de câmera 3D
- `Chunk` - Pedaço do mundo (16x16x16 blocos)
- `Block` - Representação de um bloco
- `Player` - Jogador e física
- `Renderer` - Sistema de renderização
- `GUI` - Interface gráfica

---

## 📦 Dependências

### Principais
- **LWJGL 3.x** - OpenGL bindings para Java
- **JOML** - Biblioteca de matemática 3D (vetores, matrizes)
- **OpenGL 3.3+** - API de renderização 3D

### Setup do Projeto
1. Criar projeto Maven/Gradle
2. Adicionar dependências LWJGL e JOML
3. Configurar build para incluir natives
4. Testar janela básica OpenGL

---

## 🔧 Boas Práticas

### Git & Colaboração
- **Usar Git desde o dia 1**
- **Estrutura de branches:** `main`, `dev`, `feature/nome`
- **Commits frequentes** com mensagens descritivas
- **Pull requests** para mudanças importantes
- **Reuniões semanais** para sincronização

### Desenvolvimento
- **Começar simples** - um cubo renderizado é melhor que nada
- **Testar integração semanalmente** - não deixar para o final
- **Documentar classes principais** - facilita colaboração
- **Ter plano B** - cortar features se necessário
- **Code review** entre membros do grupo

### Debugging
- **Logs detalhados** para OpenGL errors
- **Wireframe mode** para debug de geometria
- **FPS counter** para monitorar performance
- **Console commands** para testes rápidos

---

## 📋 Checklist de Entrega

### Código
- [ ] Código compilando sem erros
- [ ] Documentação JavaDoc nas classes principais
- [ ] README.md com instruções de execução
- [ ] Estrutura de projeto organizada

### Apresentação
- [ ] Demo funcionando
- [ ] Slides explicando arquitetura
- [ ] Cada membro apresenta sua parte
- [ ] Mostrar código relevante

### Requisitos POO Obrigatórios

#### Parte A: Fundamentos de POO (Nota 0-10)
- [ ] **Req. 1:** Programa estruturado em classes com encapsulamento
- [ ] **Req. 2:** Mínimo de 5 classes no programa
- [ ] **Req. 3:** Pelo menos uma classe abstrata
- [ ] **Req. 4:** Pelo menos um método abstrato
- [ ] **Req. 5:** Classes com no mínimo 10 atributos e 10 métodos (total)
- [ ] **Req. 6:** Duas relações de herança entre classes
- [ ] **Req. 7:** Método sobrescrito em subclasse (implementação de método abstrato)
- [ ] **Req. 8:** Chamada polimórfica de método
- [ ] **Req. 9:** Relação de associação entre classes (multiplicidade)
- [ ] **Req. 10:** Uso de coleção de objetos (ArrayList)

#### Parte B: Recursos Complementares (Nota 0-10)
- [ ] **Req. 11:** Classe derivada de Exception
- [ ] **Req. 12:** Interface gráfica
- [ ] **Req. 13:** Leitura de arquivo CSV ou TXT
- [ ] **Req. 14:** Recuperação e salvamento de objetos persistentes

**Nota Final = (Média A × 0,4) + (Média B × 0,6)**

---

## 🆘 Plano de Contingência

Se algo der muito errado, focar no **MVP absoluto:**
1. Mundo estático com cubos
2. Jogador que se move
3. Uma ação (quebrar OU colocar bloco)
4. Interface mínima
