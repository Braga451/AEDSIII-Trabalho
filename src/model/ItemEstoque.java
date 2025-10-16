package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemEstoque {

    protected int id;
    protected String nome;
    protected int quantidade;
    protected double precoUnitario;
    protected long dataCadastro; // Armazenaremos como um long (timestamp)
    protected int idCategoria;
    protected int idFornecedor;

    public ItemEstoque() {
        this.id = -1;
        this.nome = "";
        this.quantidade = 0;
        this.precoUnitario = 0.0;
        this.dataCadastro = System.currentTimeMillis(); // Padrão é a data/hora atual
        this.idCategoria = -1;
        this.idFornecedor = -1;
    }

    public ItemEstoque(String nome, int quantidade, double precoUnitario, int idCategoria, int idFornecedor) {
        this.id = -1;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.dataCadastro = System.currentTimeMillis();
        this.idCategoria = idCategoria;
        this.idFornecedor = idFornecedor;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public long getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(long dataCadastro) { this.dataCadastro = dataCadastro; }
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }
    
    // Método auxiliar para exibir a data de forma legível
    public String getDataCadastroFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.dataCadastro));
    }

    @Override
    public String toString() {
        return "ItemEstoque [" +
               "ID: " + id +
               ", Nome: '" + nome + '\'' +
               ", Qtd: " + quantidade +
               ", Preço: " + String.format("%.2f", precoUnitario) +
               ", Cadastro: '" + getDataCadastroFormatada() + '\'' +
               ", ID Categoria: " + idCategoria +
               ", ID Fornecedor: " + idFornecedor +
               ']';
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeInt(this.quantidade);
        dos.writeDouble(this.precoUnitario);
        dos.writeLong(this.dataCadastro);
        dos.writeInt(this.idCategoria);
        dos.writeInt(this.idFornecedor);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.quantidade = dis.readInt();
        this.precoUnitario = dis.readDouble();
        this.dataCadastro = dis.readLong();
        this.idCategoria = dis.readInt();
        this.idFornecedor = dis.readInt();
    }
}