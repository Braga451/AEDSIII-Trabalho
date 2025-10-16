package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import indices.ArvoreBPlus;  // Importa a Arvore B+
import indices.HashExtensivel;
import model.ItemEstoque;

public class ItemEstoqueDAO {

    private final String DB_FILE = "data/itens_estoque.db";
    private RandomAccessFile raf;
    private HashExtensivel indicePrimario;      // Para a chave primária (ID)
    private ArvoreBPlus indiceSecundario; // Para a chave estrangeira (idCategoria)

    private final int HEADER_SIZE = 4;

    public ItemEstoqueDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");

        // Inicializa o índice primário (Hash)
        indicePrimario = new HashExtensivel("itens_pk");

        // Inicializa o índice secundário (B+ Tree)
        indiceSecundario = new ArvoreBPlus("itens_fk_categoria");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }
    }
    
    public ItemEstoque create(ItemEstoque item) throws IOException {
        raf.seek(0);
        int ultimoID = raf.readInt();
        int novoID = ultimoID + 1;
        item.setId(novoID);
        raf.seek(0);
        raf.writeInt(novoID);
        
        raf.seek(raf.length());
        long enderecoRegistro = raf.getFilePointer();
        
        raf.writeByte(' '); // Lápide
        byte[] recordBytes = item.toByteArray();
        raf.writeInt(recordBytes.length);
        raf.write(recordBytes);
        
        // Atualiza AMBOS os índices
        indicePrimario.create(novoID, enderecoRegistro);
        indiceSecundario.create(item.getIdCategoria(), enderecoRegistro);
        
        return item;
    }

    // Busca por ID (PK) continua usando o Hash
    public ItemEstoque read(int id) throws IOException {
        long endereco = indicePrimario.read(id);
        if (endereco == -1) return null;

        raf.seek(endereco);
        byte lapide = raf.readByte();
        int recordSize = raf.readInt();
        byte[] recordBytes = new byte[recordSize];
        raf.read(recordBytes);

        if (lapide == ' ') {
            ItemEstoque item = new ItemEstoque();
            item.fromByteArray(recordBytes);
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * ESTE É O NOVO MÉTODO QUE ESTAVA FALTANDO.
     * Busca todos os itens que pertencem a uma categoria.
     * Usa a Árvore B+ para eficiência.
     * @param idCategoria A chave estrangeira a ser buscada.
     * @return Uma lista de objetos ItemEstoque.
     */
    public List<ItemEstoque> readAllByIdCategoria(int idCategoria) throws IOException {
        List<ItemEstoque> listaItens = new ArrayList<>();
        
        List<Long> enderecos = indiceSecundario.readAll(idCategoria);
        
        for (long endereco : enderecos) {
            raf.seek(endereco);
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);

            if (lapide == ' ') {
                ItemEstoque item = new ItemEstoque();
                item.fromByteArray(recordBytes);
                listaItens.add(item);
            }
        }
        
        return listaItens;
    }

    public boolean update(ItemEstoque item) throws IOException {
        ItemEstoque itemAntigo = read(item.getId());
        if (itemAntigo == null) return false;
        
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);

            if (lapide == ' ') {
                ItemEstoque temp = new ItemEstoque();
                temp.fromByteArray(recordBytes);
                if (temp.getId() == item.getId()) {
                    byte[] newRecordBytes = item.toByteArray();
                    if (newRecordBytes.length <= recordSize) {
                        raf.seek(currentPos + 1 + 4);
                        raf.write(newRecordBytes);
                    } else {
                        delete(item.getId());
                        create(item);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean delete(int id) throws IOException {
        long endereco = indicePrimario.read(id);
        if (endereco == -1) return false;

        raf.seek(endereco);
        raf.writeByte('*');
        
        indicePrimario.delete(id);
        // NOTA: A remoção da Árvore B+ é complexa e não foi implementada.
        
        return true;
    }

    public void close() throws IOException {
        raf.close();
        indicePrimario.close();
        indiceSecundario.close();
    }
}