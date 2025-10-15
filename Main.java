import libs.dao.CategoriaDAO;
import libs.sgbd.SGBD;

class Main {
    public static void main(String[] args) {
        CategoriaDAO example = new CategoriaDAO("xyz", "abc");

        example.insertCategoria();
        // System.out.println(SGBD.getInstance().getTableSize("Categoria"));
    }
}