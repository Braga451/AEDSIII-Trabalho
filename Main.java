import libs.dao.CategoriaDAO;
import libs.dao.FornecedorDAO;
import libs.sgbd.SGBD;

class Main {
    public static void main(String[] args) {
        FornecedorDAO fornecedorDAO = new FornecedorDAO("Silva");

        fornecedorDAO.insertFornecedor();
    }
}