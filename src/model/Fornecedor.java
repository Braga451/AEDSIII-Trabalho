package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fornecedor {

    protected int id;
    protected String nome;
    protected String cnpj;
    protected String endereco;
    protected List<String> telefones;

    public Fornecedor() {
        this.id = -1;
        this.nome = "";
        this.cnpj = "";
        this.endereco = "";
        this.telefones = new ArrayList<>();
    }

    public Fornecedor(String nome, String cnpj, String endereco, List<String> telefones) {
        this.id = -1; // Controlado pelo DAO
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefones = telefones;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public List<String> getTelefones() { return telefones; }
    public void setTelefones(List<String> telefones) { this.telefones = telefones; }

    @Override
    public String toString() {
        return "Fornecedor [" +
               "ID: " + id +
               ", Nome: '" + nome + '\'' +
               ", CNPJ: '" + cnpj + '\'' +
               ", Endereço: '" + endereco + '\'' +
               ", Telefones: " + telefones +
               ']';
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.cnpj);
        dos.writeUTF(this.endereco);

        // Converte a lista de telefones em uma única string separada por ';'
        String telefonesStr = String.join(";", this.telefones);
        dos.writeUTF(telefonesStr);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.cnpj = dis.readUTF();
        this.endereco = dis.readUTF();

        // Lê a string única de telefones e a converte de volta para uma lista
        String telefonesStr = dis.readUTF();
        if (telefonesStr.isEmpty()) {
            this.telefones = new ArrayList<>();
        } else {
            this.telefones = new ArrayList<>(Arrays.asList(telefonesStr.split(";")));
        }
    }
}