package libs.dao;

import libs.dao.annotations.DatabaseField;
import libs.dao.annotations.ForeignKey;
import libs.dao.annotations.NtoNRelationship;
import libs.dao.annotations.PrimaryKey;
import libs.model.FornecedorModel;
import libs.model.ItemFornecedorModel;
import libs.model.ItemModel;

import java.util.ArrayList;

public class ItemDAO extends GeneralDao {
    @PrimaryKey
    @DatabaseField(fieldName = "id", notNull = true)
    private Integer id;

    @DatabaseField(fieldName = "nome", notNull = true)
    private String nome;

    @DatabaseField(fieldName = "descricao", notNull = true)
    private String descricao;

    @DatabaseField(fieldName = "quantidade_estoque", notNull = true)
    private Integer quantidade_estoque;

    @ForeignKey(tableReference = "Categoria", keyReference = "id")
    @DatabaseField(fieldName = "id_categoria", notNull = true)
    private Integer id_categoria;

    @NtoNRelationship(tableName = "Item_Fornecedor", tableReference = "Fornecedor")
    private ArrayList<Item_FornecedorDAO> item_fornecedor;

    public ItemDAO(String nome, String descricao, Integer quantidade_estoque, Integer id_categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade_estoque = quantidade_estoque;
        this.id_categoria = id_categoria;

        this.item_fornecedor = new ArrayList<>();

        this.tableName = "Item";
    }

    public ItemModel insertItem() {
        this.insert();

        return new ItemModel(this.id,
                this.nome,
                this.descricao,
                this.quantidade_estoque,
                this.id_categoria,
                this.item_fornecedor
        );
    }

    @Override
    protected Integer returnPrimaryKey() {
        return this.id;
    }

    @Override
    protected void setPrimaryKey(Integer primaryKey) {
        this.id = primaryKey;
    }
}
