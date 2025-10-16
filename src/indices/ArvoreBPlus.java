package indices;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ArvoreBPlus {

    private String fileName;
    private RandomAccessFile raf;
    private long raiz; // Endereço da página raiz no arquivo

    // O cabeçalho do arquivo da árvore guarda o endereço da raiz
    private final int HEADER_SIZE = 8; 

    public ArvoreBPlus(String nomeBase) throws IOException {
        this.fileName = "data/" + nomeBase + "_bplus.db";
        this.raf = new RandomAccessFile(fileName, "rw");

        if (raf.length() == 0) {
            // Arquivo novo: cria a primeira página, que é a raiz e uma folha
            Pagina raizInicial = new Pagina();
            raizInicial.isLeaf = true;
            
            // A raiz começa no endereço 0 (logo após o cabeçalho)
            this.raiz = 0;
            raf.seek(HEADER_SIZE);
            raf.write(raizInicial.toByteArray());
            
            // Escreve o cabeçalho
            raf.seek(0);
            raf.writeLong(this.raiz);

        } else {
            // Arquivo existente: lê o endereço da raiz do cabeçalho
            raf.seek(0);
            this.raiz = raf.readLong();
        }
    }

    /**
     * Busca por todos os endereços associados a uma chave.
     * @param chave A chave a ser buscada (ex: idCategoria).
     * @return Uma lista de endereços de registros no arquivo de dados.
     */
    public List<Long> readAll(int chave) throws IOException {
        List<Long> resultados = new ArrayList<>();
        
        // Começa a busca pela raiz
        long paginaAtualAddr = this.raiz;
        Pagina p = lerPagina(paginaAtualAddr);

        // 1. Desce a árvore até encontrar o nó folha correto
        while (!p.isLeaf) {
            int i = 0;
            while (i < p.quantidade && chave >= p.chaves[i]) {
                i++;
            }
            paginaAtualAddr = p.enderecos[i];
            p = lerPagina(paginaAtualAddr);
        }

        // 2. Percorre os nós folha usando os ponteiros de "próximo"
        boolean continuar = true;
        while (continuar && p != null) {
            for (int i = 0; i < p.quantidade; i++) {
                if (p.chaves[i] == chave) {
                    resultados.add(p.enderecos[i]);
                } else if (p.chaves[i] > chave) {
                    // Como as chaves são ordenadas, não precisamos procurar mais
                    continuar = false; 
                    break;
                }
            }
            
            if (continuar) {
                if (p.getProximo() != -1) {
                    paginaAtualAddr = p.getProximo();
                    p = lerPagina(paginaAtualAddr);
                } else {
                    p = null; // Fim da lista de folhas
                }
            }
        }
        
        return resultados;
    }

    // A inserção e a divisão de nós em uma B+ Tree são operações complexas.
    // Para a entrega, uma implementação simplificada que não lida com splits
    // pode ser um ponto de partida, mas o ideal é ter a lógica completa.
    // A implementação abaixo é uma versão SIMPLIFICADA e didática.
    // Ela NÃO implementa a subida de chaves ou divisão de nós internos.
    
    public void create(int chave, long endereco) throws IOException {
        long paginaAtualAddr = this.raiz;
        Pagina p = lerPagina(paginaAtualAddr);

        // Encontra a página folha onde a chave deve ser inserida
        while(!p.isLeaf){
            int i = 0;
            while (i < p.quantidade && chave >= p.chaves[i]) {
                i++;
            }
            paginaAtualAddr = p.enderecos[i];
            p = lerPagina(paginaAtualAddr);
        }

        // Se a página tem espaço, insere e pronto.
        if (!p.isFull()) {
            inserirEmFolha(p, chave, endereco);
            escreverPagina(paginaAtualAddr, p);
            return;
        }

        // LÓGICA DE SPLIT SIMPLIFICADA (para fins didáticos)
        // Cria uma nova página "irmã"
        Pagina novaPagina = new Pagina();
        long novaPaginaAddr = raf.length();

        // Conecta as duas páginas
        novaPagina.setProximo(p.getProximo());
        p.setProximo(novaPaginaAddr);

        // Copia a metade superior dos dados para a nova página
        int meio = (Pagina.ORDEM - 1) / 2;
        for (int i = meio; i < Pagina.ORDEM - 1; i++) {
            novaPagina.chaves[i - meio] = p.chaves[i];
            novaPagina.enderecos[i - meio] = p.enderecos[i];
            novaPagina.quantidade++;
        }
        p.quantidade = meio;

        // Decide onde inserir a nova chave
        if (chave < p.chaves[meio-1]) {
            inserirEmFolha(p, chave, endereco);
        } else {
            inserirEmFolha(novaPagina, chave, endereco);
        }
        
        escreverPagina(paginaAtualAddr, p);
        escreverPagina(novaPaginaAddr, novaPagina);
        
        // NOTA: Esta versão não promove a chave para o nó pai, o que é uma
        // simplificação significativa do algoritmo completo. Para o escopo deste
        // trabalho, focar na busca e na estrutura de folhas é o mais crucial.
    }
    
    private void inserirEmFolha(Pagina p, int chave, long endereco) {
        int i = p.quantidade;
        // Encontra a posição correta para inserção mantendo a ordem
        while (i > 0 && p.chaves[i-1] > chave) {
            p.chaves[i] = p.chaves[i-1];
            p.enderecos[i] = p.enderecos[i-1];
            i--;
        }
        p.chaves[i] = chave;
        p.enderecos[i] = endereco;
        p.quantidade++;
    }

    private Pagina lerPagina(long endereco) throws IOException {
        raf.seek(HEADER_SIZE + endereco);
        byte[] buffer = new byte[Pagina.TAMANHO_PAGINA];
        raf.read(buffer);
        Pagina p = new Pagina();
        p.fromByteArray(buffer);
        return p;
    }

    private void escreverPagina(long endereco, Pagina p) throws IOException {
        raf.seek(HEADER_SIZE + endereco);
        raf.write(p.toByteArray());
    }

    public void close() throws IOException {
        raf.close();
    }
}