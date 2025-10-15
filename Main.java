import libs.dao.CategoriaDAO;
import libs.sgbd.SGBD;

class Main {
    public static void main(String[] args) {
        SGBD sgbd = SGBD.getInstance();

        System.out.println(sgbd);

        CategoriaDAO example = new CategoriaDAO("xyz", "abc");

        example.insertCategoria();
    }
}