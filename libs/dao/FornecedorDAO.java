package libs.dao;

import libs.model.FornecedorModel;

// TODO: Implement 1-n table for multivalue register (CONTATO_FORNECEDOR)
public class FornecedorDAO extends GeneralDao {
    private Integer id;
    private final String nome;

    public FornecedorDAO(String nome) {
        this.nome = nome;
        this.tableName = "Fornecedor";
    }

    public FornecedorModel insertFornecedor() {
        this.insert();

        return new FornecedorModel(this.nome);
    }
}
