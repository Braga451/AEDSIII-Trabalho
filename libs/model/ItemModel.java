package libs.model;

import libs.dao.Item_FornecedorDAO;

import java.util.ArrayList;

public class ItemModel {
    private Integer id;
    private String nome;
    private String descricao;
    private Integer quantidade_estoque;
    private Integer id_categoria;

    private ArrayList<ItemFornecedorModel> item_fornecedor;

    public ItemModel(Integer id,
                    String nome,
                     String descricao,
                     Integer quantidade_estoque,
                     Integer id_categoria,
                     ArrayList<Item_FornecedorDAO> itemFornecedor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade_estoque = quantidade_estoque;
        this.id_categoria = id_categoria;

        this.item_fornecedor = new ArrayList<>();

        for (Item_FornecedorDAO itemFornecedorDAO : itemFornecedor) {
            this.item_fornecedor.add(new ItemFornecedorModel(itemFornecedorDAO));
        }
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade_estoque() {
        return quantidade_estoque;
    }

    public void setQuantidade_estoque(Integer quantidade_estoque) {
        this.quantidade_estoque = quantidade_estoque;
    }

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id_categoria) {
        this.id_categoria = id_categoria;
    }
}