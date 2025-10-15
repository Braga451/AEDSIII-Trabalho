package libs.dao;

import libs.model.CategoriaModel;

public class CategoriaDAO extends GeneralDao {
    private Integer id;
    private final String nome;
    private final String descricao;

    public CategoriaDAO(String nome, String  descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.tableName = "Categoria";
    }

    public CategoriaModel insertCategoria() {
        this.insert();

        return new CategoriaModel(this.nome, this.descricao);
    }
}
