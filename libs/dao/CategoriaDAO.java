package libs.dao;

import libs.dao.annotations.DatabaseField;
import libs.dao.annotations.PrimaryKey;
import libs.model.CategoriaModel;
import libs.sgbd.types.SGBDTypes;

public class CategoriaDAO extends GeneralDao {
    @DatabaseField(fieldName = "id")
    @PrimaryKey
    private Integer id;

    @DatabaseField(fieldName = "nome")
    private final String nome;

    @DatabaseField(fieldName = "descricao")
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

    @Override
    protected Integer returnPrimaryKey() {
        return this.id;
    }

    @Override
    protected void setPrimaryKey(Integer primaryKey) {
        this.id = primaryKey;
    }
}
