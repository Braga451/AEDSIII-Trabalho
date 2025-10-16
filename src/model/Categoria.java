package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Categoria {

    protected int id;
    protected String nome;
    protected String descricao;

    // Construtor padrão
    public Categoria() {
        this.id = -1;
        this.nome = "";
        this.descricao = "";
    }

    // Construtor com parâmetros
    public Categoria(String nome, String descricao) {
        this.id = -1; // O ID será controlado pelo DAO
        this.nome = nome;
        this.descricao = descricao;
    }
    
    // Getters e Setters (não mudei aqui)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "Categoria [" +
               "ID: " + id +
               ", Nome: '" + nome + '\'' +
               ", Descrição: '" + descricao + '\'' +
               ']';
    }

    /**
     * Serializa o objeto para um array de bytes de TAMANHO FIXO.
     * @return Array de bytes representando o objeto.
     * @throws IOException
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(this.id);
        
        // Escreve a string 'nome' com tamanho fixo (ex: 50 bytes)
        dos.writeUTF(this.nome); 
        
        // Escreve a string 'descricao' com tamanho fixo (ex: 100 bytes)
        dos.writeUTF(this.descricao);
        
        return baos.toByteArray();
    }

    /**
     * Deserializa um array de bytes de volta para o objeto.
     * @param ba O array de bytes lido do arquivo.
     * @throws IOException
     */
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
    }
}