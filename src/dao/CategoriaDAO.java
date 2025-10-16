package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import indices.HashExtensivel; // 1. IMPORTAR A CLASSE DO ÍNDICE
import model.Categoria;

public class CategoriaDAO {

    private final String DB_FILE = "data/categorias.db";
    private RandomAccessFile raf;
    private HashExtensivel indice; // 2. ADICIONAR O ÍNDICE COMO ATRIBUTO

    private final int HEADER_SIZE = 4;

    public CategoriaDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");

        // 3. INICIALIZAR O ÍNDICE
        // O nome "categorias_pk" será a base para os arquivos do índice.
        // Serão criados "data/categorias_pk_dir.db" e "data/categorias_pk_cestos.db"
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
        
        // Posiciona no final para escrever o novo registro
        raf.seek(raf.length());
        
        // 4. CAPTURAR O ENDEREÇO ANTES DE ESCREVER
        long enderecoRegistro = raf.getFilePointer();
        
        raf.writeByte(' '); // Lápide
        byte[] recordBytes = categoria.toByteArray();
        raf.writeInt(recordBytes.length);
        raf.write(recordBytes);
        
        // 5. ADICIONAR O NOVO PAR [ID, ENDEREÇO] AO ÍNDICE
        indice.create(novoID, enderecoRegistro);
        
        return categoria;
    }

    /**
     * Busca uma Categoria pelo seu ID usando o índice HASH.
     * Esta é a versão RÁPIDA e OTIMIZADA.
     */
    public Categoria read(int id) throws IOException {
        // 1. Pede ao índice o endereço do registro com o ID fornecido
        long endereco = indice.read(id);

        // 2. Se o endereço não for encontrado (-1), o registro não existe
        if (endereco == -1) {
            return null;
        }

        // 3. Pula DIRETAMENTE para a posição do registro no arquivo de dados
        raf.seek(endereco);
        
        // 4. Lê e retorna o registro encontrado
        byte lapide = raf.readByte();
        int recordSize = raf.readInt();
        byte[] recordBytes = new byte[recordSize];
        raf.read(recordBytes);

        if (lapide == ' ') {
            Categoria categoria = new Categoria();
            categoria.fromByteArray(recordBytes);
            // Confirmação extra (boa prática)
            if (categoria.getId() == id) {
                return categoria;
            }
        }
        
        return null; // Caso encontre um registro "fantasma" ou com lápide
    }

    public boolean update(Categoria categoria) throws IOException {
        // Usa a nova busca rápida para encontrar o registro
        Categoria categoriaAntiga = read(categoria.getId());
        if (categoriaAntiga == null) {
            return false; // Não pode atualizar um registro que não existe
        }
        
        // Para o update, ainda precisamos encontrar a posição original do registro
        // A busca sequencial ainda é necessária aqui para encontrar a POSIÇÃO
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
                        // O endereço não mudou, então o índice não precisa ser atualizado.
                    } else {
                        // Se não couber, exclui o antigo e cria um novo.
                        // Nossos métodos delete e create JÁ atualizam o índice.
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
        // Usa a nova busca rápida para ver se o registro existe
        long endereco = indice.read(id);
        if (endereco == -1) {
            return false;
        }

        // Vai até a posição e marca a lápide
        raf.seek(endereco);
        raf.writeByte('*');
        
        // REMOVE A CHAVE DO ÍNDICE para que não seja mais encontrada
        indice.delete(id);

        return true;
    }

    public void close() throws IOException {
        raf.close();
        indice.close(); // 6. FECHAR OS ARQUIVOS DO ÍNDICE
    }
}