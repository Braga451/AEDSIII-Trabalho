package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import indices.HashExtensivel;
import model.Fornecedor;
import seguranca.CriptografiaRSA; // Importação da classe de segurança

public class FornecedorDAO {

    private final String DB_FILE = "data/fornecedores.db";
    private RandomAccessFile raf;
    private HashExtensivel indice;
    private CriptografiaRSA rsa; // Declaração do objeto RSA

    private final int HEADER_SIZE = 4;

    public FornecedorDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");
        indice = new HashExtensivel("fornecedores_pk");
        
        // Inicializa a criptografia (vai carregar ou gerar as chaves)
        this.rsa = new CriptografiaRSA();

        if (raf.length() == 0) {
            raf.writeInt(0);
        }
    }
    
    public Fornecedor create(Fornecedor fornecedor) throws IOException {
        raf.seek(0);
        int ultimoID = raf.readInt();
        int novoID = ultimoID + 1;
        fornecedor.setId(novoID);
        raf.seek(0);
        raf.writeInt(novoID);
        
        raf.seek(raf.length());
        long enderecoRegistro = raf.getFilePointer();
        
        // --- INÍCIO DA CRIPTOGRAFIA ---
        String cnpjOriginal = fornecedor.getCnpj(); // Salva o original
        try {
            // Criptografa o CNPJ antes de gerar os bytes
            String cnpjCifrado = rsa.criptografar(cnpjOriginal);
            fornecedor.setCnpj(cnpjCifrado);
            
            // Agora gera os bytes com o CNPJ criptografado
            byte[] recordBytes = fornecedor.toByteArray();
            
            raf.writeByte(' '); 
            raf.writeInt(recordBytes.length);
            raf.write(recordBytes);
            
            indice.create(novoID, enderecoRegistro);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erro ao criptografar dados: " + e.getMessage());
        } finally {
            // IMPORTANTE: Restaura o CNPJ original no objeto em memória.
            // Se não fizermos isso, a tela do usuário vai mostrar o código maluco
            // logo após clicar em "Salvar".
            fornecedor.setCnpj(cnpjOriginal);
        }
        // --- FIM DA CRIPTOGRAFIA ---
        
        return fornecedor;
    }

    public Fornecedor read(int id) throws IOException {
        long endereco = indice.read(id);
        if (endereco == -1) return null;

        raf.seek(endereco);
        byte lapide = raf.readByte();
        int recordSize = raf.readInt();
        byte[] recordBytes = new byte[recordSize];
        raf.read(recordBytes);

        if (lapide == ' ') {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.fromByteArray(recordBytes); // Aqui o CNPJ está criptografado
            
            if (fornecedor.getId() == id) {
                // --- DESCRIPTOGRAFIA ---
                try {
                    String cnpjCifrado = fornecedor.getCnpj();
                    String cnpjDescifrado = rsa.descriptografar(cnpjCifrado);
                    fornecedor.setCnpj(cnpjDescifrado); // Restaura o CNPJ legível
                } catch (Exception e) {
                    System.err.println("Erro ao descriptografar fornecedor ID " + id);
                    // Em caso de erro, mantemos o cifrado para não quebrar o fluxo
                }
                // -----------------------
                return fornecedor;
            }
        }
        return null;
    }

    public boolean update(Fornecedor fornecedor) throws IOException {
        Fornecedor fornecedorAntigo = read(fornecedor.getId());
        if (fornecedorAntigo == null) return false;
        
        // 1. Criptografia (igual antes)
        String cnpjOriginal = fornecedor.getCnpj();
        try {
            String cnpjCifrado = rsa.criptografar(cnpjOriginal);
            fornecedor.setCnpj(cnpjCifrado);
            
            byte[] newRecordBytes = fornecedor.toByteArray();
            
            // Busca sequencial para achar o antigo
            raf.seek(HEADER_SIZE);
            while (raf.getFilePointer() < raf.length()) {
                long currentPos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int recordSize = raf.readInt();
                
                if (lapide == ' ') {
                    // Pula os dados para verificar ID ou ler se necessário
                    // Melhor ler para confirmar ID
                    byte[] recordBytes = new byte[recordSize];
                    raf.read(recordBytes);
                    Fornecedor temp = new Fornecedor();
                    temp.fromByteArray(recordBytes);

                    if (temp.getId() == fornecedor.getId()) {
                        
                        // CENÁRIO A: Cabe no mesmo lugar
                        if (newRecordBytes.length <= recordSize) {
                            raf.seek(currentPos + 1 + 4); // Pula lápide e tamanho
                            raf.write(newRecordBytes);
                            return true;
                        } 
                        // CENÁRIO B: Não cabe (Correção do BUG aqui!)
                        else {
                            // 1. Marca o antigo como deletado
                            raf.seek(currentPos);
                            raf.writeByte('*');
                            
                            // 2. Vai para o final do arquivo
                            raf.seek(raf.length());
                            long novoEndereco = raf.getFilePointer();
                            
                            // 3. Escreve o registro NOVO com o ID ANTIGO
                            raf.writeByte(' ');
                            raf.writeInt(newRecordBytes.length);
                            raf.write(newRecordBytes);
                            
                            // 4. Atualiza o índice para apontar para o novo endereço
                            // O HashExtensivel.update(id, novoEndereco) deve resolver
                            // Se o seu Hash não tem update, usamos:
                            // indice.delete(fornecedor.getId());
                            // indice.create(fornecedor.getId(), novoEndereco);
                            
                            // Assumindo que seu HashExtensivel tem um método update que aceita (id, endereço)
                            // Se não tiver, use a lógica de delete + create COM O MESMO ID:
                            indice.update(fornecedor.getId(), novoEndereco); 
                            
                            return true;
                        }
                    }
                } else {
                    raf.skipBytes(recordSize); // Pula registro deletado
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            fornecedor.setCnpj(cnpjOriginal);
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
    
    // Adicionei o listAll aqui também para garantir que a listagem mostre os dados corretos
    public List<Fornecedor> listAll() throws IOException {
        List<Fornecedor> lista = new ArrayList<>();
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            
            if (lapide == ' ') {
                byte[] recordBytes = new byte[recordSize];
                raf.read(recordBytes);
                Fornecedor obj = new Fornecedor();
                obj.fromByteArray(recordBytes);
                
                // --- DESCRIPTOGRAFIA NA LISTAGEM ---
                try {
                    String cnpjDescifrado = rsa.descriptografar(obj.getCnpj());
                    obj.setCnpj(cnpjDescifrado);
                } catch (Exception e) {
                   // Se falhar, vai mostrar criptografado mesmo
                }
                // -----------------------------------
                
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