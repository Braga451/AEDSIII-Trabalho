package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Pagina {
    
    public static final int ORDEM = 5;
    // O tamanho em bytes de uma página no arquivo.
    // 1 (isLeaf) + 4 (quantidade) + (ORDEM-1)*4 (chaves) + ORDEM*8 (enderecos)
    public static final int TAMANHO_PAGINA = 1 + 4 + (ORDEM - 1) * 4 + ORDEM * 8; 

    protected boolean isLeaf;   // TRUE se for nó folha, FALSE se for nó interno
    protected int quantidade;   // Quantidade de chaves na página
    protected int[] chaves;     // As chaves (idCategoria)
    protected long[] enderecos; // Endereços de dados (folha) ou ponteiros para filhos (interno)

    public Pagina() {
        this.isLeaf = true; // Por padrão, uma nova página é uma folha
        this.quantidade = 0;
        this.chaves = new int[ORDEM - 1];
        this.enderecos = new long[ORDEM];
        for (int i = 0; i < ORDEM - 1; i++) {
            chaves[i] = -1;
            enderecos[i] = -1;
        }
        enderecos[ORDEM - 1] = -1; // No nó folha, este último ponteiro aponta para a próxima folha
    }

    public boolean isFull() {
        return quantidade >= ORDEM - 1;
    }

    // O último endereço em um nó folha aponta para a próxima página folha (irmão)
    public long getProximo() {
        if (!isLeaf) return -1; // Só faz sentido para folhas
        return enderecos[ORDEM - 1];
    }
    
    public void setProximo(long proximo) {
        if (!isLeaf) return;
        enderecos[ORDEM - 1] = proximo;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeBoolean(isLeaf);
        dos.writeInt(quantidade);
        for (int i = 0; i < ORDEM - 1; i++) {
            dos.writeInt(chaves[i]);
            dos.writeLong(enderecos[i]);
        }
        dos.writeLong(enderecos[ORDEM - 1]); // Escreve o último endereço (ponteiro de filho ou próximo)
        
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.isLeaf = dis.readBoolean();
        this.quantidade = dis.readInt();
        for (int i = 0; i < ORDEM - 1; i++) {
            this.chaves[i] = dis.readInt();
            this.enderecos[i] = dis.readLong();
        }
        this.enderecos[ORDEM - 1] = dis.readLong();
    }
}