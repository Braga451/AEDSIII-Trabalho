package libs.model;

import java.util.ArrayList;

public class FornecedorModel {
    private Integer id;
    private String nome;
    private ArrayList<ContatoFornecedorModel> contatoFornecedor;

    private ArrayList<ItemFornecedorModel> item_fornecedor;

    public FornecedorModel(String nome) {
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<ContatoFornecedorModel> getContatoFornecedor() {
        return contatoFornecedor;
    }

    public void setContatoFornecedor(ArrayList<ContatoFornecedorModel> contatoFornecedor) {
        this.contatoFornecedor = contatoFornecedor;
    }

    public ArrayList<ItemFornecedorModel> getItem_fornecedor() {
        return item_fornecedor;
    }

    public void setItem_fornecedor(ArrayList<ItemFornecedorModel> item_fornecedor) {
        this.item_fornecedor = item_fornecedor;
    }
}