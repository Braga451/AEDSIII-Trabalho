package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import indices.HashExtensivel;
import model.Categoria;

public class CategoriaDAO {

    private final String DB_FILE = "data/categorias.db";
    private RandomAccessFile raf;
    private HashExtensivel indice;

    private final int HEADER_SIZE = 4;

    public CategoriaDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");
        indice = new HashExtensivel("categorias_pk");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }
    }
    
    public Categoria create(Categoria categoria) throws IOException {
        raf.seek(0);
        int ultimoID = raf.readInt();
        int novoID = ultimoID + 1;
        categoria.setId(novoID);
        raf.seek(0);
        raf.writeInt(novoID);
        
        raf.seek(raf.length());
        long enderecoRegistro = raf.getFilePointer();
        
        raf.writeByte(' '); 
        byte[] recordBytes = categoria.toByteArray();
        raf.writeInt(recordBytes.length);
        raf.write(recordBytes);
        
        indice.create(novoID, enderecoRegistro);
        
        return categoria;
    }

    public Categoria read(int id) throws IOException {
        long endereco = indice.read(id);
        if (endereco == -1) return null;

        raf.seek(endereco);
        byte lapide = raf.readByte();
        int recordSize = raf.readInt();
        byte[] recordBytes = new byte[recordSize];
        raf.read(recordBytes);

        if (lapide == ' ') {
            Categoria categoria = new Categoria();
            categoria.fromByteArray(recordBytes);
            if (categoria.getId() == id) {
                return categoria;
            }
        }
        return null;
    }

    public boolean update(Categoria categoria) throws IOException {
        Categoria categoriaAntiga = read(categoria.getId());
        if (categoriaAntiga == null) return false;
        
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);

            if (lapide == ' ') {
                Categoria temp = new Categoria();
                temp.fromByteArray(recordBytes);
                
                if (temp.getId() == categoria.getId()) {
                    byte[] newRecordBytes = categoria.toByteArray();
                    if (newRecordBytes.length <= recordSize) {
                        raf.seek(currentPos + 1 + 4);
                        raf.write(newRecordBytes);
                    } else {
                        delete(categoria.getId());
                        create(categoria);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean delete(int id) throws IOException {
        long endereco = indice.read(id);
        if (endereco == -1) return false;

        raf.seek(endereco);
        raf.writeByte('*');
        indice.delete(id);
        return true;
    }

    // --- O MÉTODO QUE FALTAVA ---
    public List<Categoria> listAll() throws IOException {
        List<Categoria> lista = new ArrayList<>();
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            
            if (lapide == ' ') {
                byte[] recordBytes = new byte[recordSize];
                raf.read(recordBytes);
                Categoria obj = new Categoria();
                obj.fromByteArray(recordBytes);
                lista.add(obj);
            } else {
                raf.seek(currentPos + 1 + 4 + recordSize);
            }
        }
        return lista;
    }

    public void close() throws IOException {
        raf.close();
        indice.close();
    }
}