package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Cesto {

    // DECISÃO DE PROJETO: Quantos pares [chave, endereço] um cesto pode conter.
    // Um valor pequeno é bom para forçar splits e testar o algoritmo.
    public static final int TAMANHO_CESTO = 4;

    // --- Atributos do Cesto ---
    private int profundidadeLocal;
    private int quantidade; // Quantidade atual de pares no cesto
    private int[] chaves; // Os IDs
    private long[] enderecos; // As posições no arquivo de dados

    public Cesto(int profundidadeLocal) {
        this.profundidadeLocal = profundidadeLocal;
        this.quantidade = 0;
        this.chaves = new int[TAMANHO_CESTO];
        this.enderecos = new long[TAMANHO_CESTO];
        // Inicializa com valores sentinela para indicar que está vazio
        for (int i = 0; i < TAMANHO_CESTO; i++) {
            chaves[i] = -1;
            enderecos[i] = -1L;
        }
    }

    public boolean isFull() {
        return this.quantidade >= TAMANHO_CESTO;
    }

    public boolean isEmpty() {
        return this.quantidade == 0;
    }

    /**
     * Tenta inserir um novo par [chave, endereço] no cesto.
     * @return true se a inserção foi bem-sucedida, false se o cesto estiver cheio.
     */
    public boolean create(int chave, long endereco) {
        if (isFull()) {
            return false;
        }
        chaves[quantidade] = chave;
        enderecos[quantidade] = endereco;
        quantidade++;
        return true;
    }

    /**
     * Busca por um endereço associado a uma chave.
     * @return O endereço se encontrado, -1L caso contrário.
     */
    public long read(int chave) {
        for (int i = 0; i < quantidade; i++) {
            if (chaves[i] == chave) {
                return enderecos[i];
            }
        }
        return -1L;
    }

    /**
     * Atualiza o endereço de uma chave existente.
     * @return true se a atualização foi bem-sucedida, false se a chave não foi encontrada.
     */
    public boolean update(int chave, long novoEndereco) {
        for (int i = 0; i < quantidade; i++) {
            if (chaves[i] == chave) {
                enderecos[i] = novoEndereco;
                return true;
            }
        }
        return false;
    }

    /**
     * Remove um par [chave, endereço] do cesto.
     * @return true se a remoção foi bem-sucedida, false se a chave não foi encontrada.
     */
    public boolean delete(int chave) {
        for (int i = 0; i < quantidade; i++) {
            if (chaves[i] == chave) {
                // Move o último elemento para a posição do removido
                chaves[i] = chaves[quantidade - 1];
                enderecos[i] = enderecos[quantidade - 1];
                // Marca a última posição como vazia
                chaves[quantidade - 1] = -1;
                enderecos[quantidade - 1] = -1L;
                quantidade--;
                return true;
            }
        }
        return false;
    }
    
    // --- Getters e Setters ---
    public int getProfundidadeLocal() { return profundidadeLocal; }
    public void setProfundidadeLocal(int p) { this.profundidadeLocal = p; }
    public int getQuantidade() { return quantidade; }
    public int[] getChaves() { return chaves; }
    public long[] getEnderecos() { return enderecos; }

    /**
     * Serializa o cesto para um array de bytes para ser salvo no arquivo de cestos.
     * Formato: [profundidadeLocal (int)] [quantidade (int)] [chave1 (int)] [endereco1 (long)] ...
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(profundidadeLocal);
        dos.writeInt(quantidade);
        for (int i = 0; i < TAMANHO_CESTO; i++) {
            dos.writeInt(chaves[i]);
            dos.writeLong(enderecos[i]);
        }
        return baos.toByteArray();
    }

    /**
     * Deserializa um array de bytes para preencher os atributos do cesto.
     */
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.profundidadeLocal = dis.readInt();
        this.quantidade = dis.readInt();
        for (int i = 0; i < TAMANHO_CESTO; i++) {
            this.chaves[i] = dis.readInt();
            this.enderecos[i] = dis.readLong();
        }
    }
}