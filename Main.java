import libs.dao.CategoriaDAO;
import libs.dao.FornecedorDAO;
import libs.dao.ItemDAO;
import libs.dao.Item_FornecedorDAO;
import libs.model.ContatoFornecedorModel;
import libs.sgbd.SGBD;

import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        ItemDAO item = new ItemDAO("abc", "efg", 10, 0);

        item.insertItem();

        Item_FornecedorDAO itemFornecedorDAO = new Item_FornecedorDAO(1, 1);

        itemFornecedorDAO.insertItemFornecedor();
    }
}