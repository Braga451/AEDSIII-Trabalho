package libs.dao;

import libs.dao.annotations.DatabaseField;
import libs.dao.annotations.ForeignKey;
import libs.dao.annotations.MultivaluedField;
import libs.dao.annotations.PrimaryKey;
import libs.model.ContatoFornecedorModel;
import libs.model.FornecedorModel;

import java.util.ArrayList;

// TODO: Implement 1-n table for multivalue register (CONTATO_FORNECEDOR)
// TODO: Create annotation for multivalue register
// TODO: Make fieldName logic work in GeneralDAO
public class FornecedorDAO extends GeneralDao {
    @DatabaseField(fieldName = "id", notNull = true)
    @PrimaryKey
    private Integer id;

    @DatabaseField(fieldName = "nome", notNull = true)
    private final String nome;

    @MultivaluedField(tableName = "Contato_Fornecedor")
    private final ArrayList<ContatoFornecedorDAO> contato_fornecedor;

    public FornecedorDAO(String nome, ArrayList<ContatoFornecedorModel> contato_fornecedor) {
        this.nome = nome;
        this.contato_fornecedor = new ArrayList<>();
        this.tableName = "Fornecedor";

        for (ContatoFornecedorModel contatoFornecedorModel : contato_fornecedor) {
            this.contato_fornecedor.add(this.ReturnContatoFornecedorDAO(contatoFornecedorModel));
        }
    }

    public FornecedorModel insertFornecedor() {
        this.insert();

        return new FornecedorModel(this.nome);
    }

    public ContatoFornecedorDAO ReturnContatoFornecedorDAO(ContatoFornecedorModel contatoFornecedorModel) {
        return new ContatoFornecedorDAO(
                null,
                contatoFornecedorModel.getContato(),
                this.id
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

    private class ContatoFornecedorDAO extends GeneralDao {
        @DatabaseField(fieldName = "id", notNull = true)
        @PrimaryKey
        private Integer id;

        @DatabaseField(fieldName = "contato", notNull = true)
        private String contato;

        @ForeignKey(tableReference = "Fornecedor", keyReference = "id")
        @DatabaseField(fieldName = "id_fornecedor", notNull = true)
        private Integer id_fornecedor;

        public ContatoFornecedorDAO(Integer id, String contato, Integer id_fornecedor) {
            this.id = id;
            this.contato = contato;
            this.id_fornecedor = id_fornecedor;
            this.tableName = "Contato_Fornecedor";
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
}
