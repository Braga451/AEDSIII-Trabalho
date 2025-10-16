package libs.model;

import libs.dao.Item_FornecedorDAO;

public class ItemFornecedorModel {
    private Integer id;
    private Integer id_item;
    private Integer id_fornecedor;

    public ItemFornecedorModel(Item_FornecedorDAO itemFornecedorDAO) {
        this.id = itemFornecedorDAO.getId();
        this.id_item = itemFornecedorDAO.getId_item();
        this.id_fornecedor = itemFornecedorDAO.getId_fornecedor();
    }

    public ItemFornecedorModel(Integer id, Integer id_item, Integer id_fornecedor) {
        this.id = id;
        this.id_item = id_item;
        this.id_fornecedor = id_fornecedor;
    }
}
