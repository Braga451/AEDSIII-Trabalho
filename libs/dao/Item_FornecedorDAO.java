package libs.dao;

import libs.dao.annotations.DatabaseField;
import libs.dao.annotations.ForeignKey;
import libs.dao.annotations.PrimaryKey;
import libs.model.ItemFornecedorModel;

public class Item_FornecedorDAO extends GeneralDao {
    @PrimaryKey
    @DatabaseField(fieldName = "id")
    private Integer id;

    @PrimaryKey(autoIncrement = false)
    @DatabaseField(fieldName = "id_item")
    @ForeignKey(tableReference = "Item", keyReference = "id")
    private Integer id_item;

    @PrimaryKey(autoIncrement = false)
    @DatabaseField(fieldName = "id_item")
    @ForeignKey(tableReference = "Item", keyReference = "id")
    private Integer id_fornecedor;

    public Item_FornecedorDAO(Integer id_item, Integer id_fornecedor) {
        this.id_item = id_item;
        this.id_fornecedor = id_fornecedor;
        this.tableName = "Item_Fornecedor";
    }

    public ItemFornecedorModel insertItemFornecedor() {
        this.insert();

        return new ItemFornecedorModel(this.id, this.id_item, this.id_fornecedor);
    }

    @Override
    protected Integer returnPrimaryKey() {
        return this.id;
    }

    @Override
    protected void setPrimaryKey(Integer primaryKey) {
        this.id = primaryKey;
    }

    public Integer getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(Integer id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public Integer getId_item() {
        return id_item;
    }

    public void setId_item(Integer id_item) {
        this.id_item = id_item;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
