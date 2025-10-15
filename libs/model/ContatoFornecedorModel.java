package libs.model;

public class ContatoFornecedorModel {
    private Integer id;
    private String contato;
    private Integer id_fornecedor;

    public ContatoFornecedorModel(Integer id, String contato, Integer id_fornecedor) {
        this.id = id;
        this.contato = contato;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Integer getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(Integer id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }
}
