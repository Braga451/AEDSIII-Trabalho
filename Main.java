import libs.dao.CategoriaDAO;
import libs.dao.FornecedorDAO;
import libs.model.ContatoFornecedorModel;
import libs.sgbd.SGBD;

import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        ArrayList<ContatoFornecedorModel> contatoFornecedorModelArrayList = new ArrayList<>();
        
        contatoFornecedorModelArrayList.add(new ContatoFornecedorModel(
                null,
                "abc",
                null
        ));
        
        FornecedorDAO fornecedorDAO = new FornecedorDAO(
                "Jose",
                contatoFornecedorModelArrayList
        );

        fornecedorDAO.insertFornecedor();
    }
}