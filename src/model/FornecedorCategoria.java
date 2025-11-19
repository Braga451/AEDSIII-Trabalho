package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa a tabela associativa (N:N) entre Fornecedor e Categoria.
 * A chave primária desta entidade é composta por (idFornecedor, idCategoria).
 */
public class FornecedorCategoria {

    protected int idFornecedor;
    protected int idCategoria;

    public FornecedorCategoria() {
        this.idFornecedor = -1;
        this.idCategoria = -1;
    }

    public FornecedorCategoria(int idFornecedor, int idCategoria) {
        this.idFornecedor = idFornecedor;
        this.idCategoria = idCategoria;
    }

    // --- Getters e Setters ---
    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public String toString() {
        return "Relacionamento [ID Fornecedor: " + idFornecedor + 
               ", ID Categoria: " + idCategoria + "]";
    }

    /**
     * Serializa o objeto para um array de bytes.
     * Formato: [idFornecedor (int)] [idCategoria (int)]
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idFornecedor);
        dos.writeInt(this.idCategoria);

        return baos.toByteArray();
    }

    /**
     * Deserializa um array de bytes para preencher o objeto.
     */
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.idFornecedor = dis.readInt();
        this.idCategoria = dis.readInt();
    }
}