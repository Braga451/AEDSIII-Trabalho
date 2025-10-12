package libs.model;

import java.util.ArrayList;

public class ItemModel {
    private Integer id;
    private String nome;
    private String descricao;
    private Integer quantidade_estoque;
    private Integer id_categoria;

    private ArrayList<ItemFornecedorModel> item_fornecedor;
}