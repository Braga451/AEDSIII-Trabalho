package compressao;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorBackup {

    public static void realizarBackup(int tipo) throws IOException {
        List<File> listaArquivos = new ArrayList<>();
        
        // 1. Adicionar arquivos .db da pasta data
        File pastaData = new File("data");
        File[] dbs = pastaData.listFiles((dir, name) -> name.endsWith(".db"));
        if (dbs != null) {
            for (File f : dbs) listaArquivos.add(f);
        }

        // 2. Adicionar chaves (.key) da pasta data/chaves
        File pastaChaves = new File("data/chaves");
        File[] chaves = pastaChaves.listFiles((dir, name) -> name.endsWith(".key"));
        if (chaves != null) {
            for (File f : chaves) listaArquivos.add(f);
        }
        
        if (listaArquivos.isEmpty()) {
            System.out.println("ERRO: Nenhum arquivo para backup.");
            return;
        }

        System.out.println("Iniciando backup de " + listaArquivos.size() + " arquivos (DBs + Chaves)...");
        String arquivoUnico = "data/temp_all_files.bin";
        
        // Juntar arquivos
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoUnico))) {
            dos.writeInt(listaArquivos.size()); 
            for (File f : listaArquivos) {
                // Truque: Se for chave, salvamos com o prefixo "chaves/" para saber onde restaurar depois
                String nomeSalvo = f.getName();
                if (f.getParent().endsWith("chaves")) {
                    nomeSalvo = "chaves/" + f.getName();
                }
                
                System.out.println(" -> Adicionando: " + nomeSalvo);
                dos.writeUTF(nomeSalvo); 
                dos.writeLong(f.length()); 
                Files.copy(f.toPath(), dos); 
            }
        }

        // Compressão (Mantida igual)
        long inicio = System.currentTimeMillis();
        long tamanhoOriginal = new File(arquivoUnico).length();
        String destino = (tipo == 1) ? "backup_huffman.cmp" : "backup_lzw.cmp";

        if (tipo == 1) {
            System.out.println("Comprimindo com Huffman...");
            Huffman.comprimir(arquivoUnico, destino);
        } else {
            System.out.println("Comprimindo com LZW...");
            LZW.comprimir(arquivoUnico, destino);
        }
        
        long fim = System.currentTimeMillis();
        new File(arquivoUnico).delete(); // Limpeza

        System.out.println("\n--- RELATÓRIO DE BACKUP ---");
        System.out.println("Arquivo: " + destino);
        System.out.println("Taxa: " + String.format("%.2f", 100.0 * (1.0 - ((double)new File(destino).length() / tamanhoOriginal))) + "%");
        System.out.println("---------------------------");
    }

    public static void restaurarBackup(int tipo) throws IOException {
        String arquivoBackup = (tipo == 1) ? "backup_huffman.cmp" : "backup_lzw.cmp";
        if (!new File(arquivoBackup).exists()) {
            System.out.println("Erro: " + arquivoBackup + " não encontrado.");
            return;
        }

        System.out.println("Iniciando restauração...");
        String arquivoTemp = "data/temp_restored.bin";

        // 1. Descomprimir
        if (tipo == 1) Huffman.descomprimir(arquivoBackup, arquivoTemp);
        else LZW.descomprimir(arquivoBackup, arquivoTemp);

        // 2. Separar arquivos
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoTemp))) {
            int qtdArquivos = dis.readInt();
            for (int i = 0; i < qtdArquivos; i++) {
                String nomeArquivo = dis.readUTF();
                long tamanho = dis.readLong();
                
                System.out.println(" -> Restaurando: " + nomeArquivo);
                
                // Lógica para restaurar na pasta certa (data/ ou data/chaves/)
                File destino;
                if (nomeArquivo.startsWith("chaves/")) {
                    new File("data/chaves").mkdirs(); // Garante que a pasta existe
                    destino = new File("data/" + nomeArquivo);
                } else {
                    destino = new File("data/" + nomeArquivo);
                }
                
                try (FileOutputStream fos = new FileOutputStream(destino)) {
                    byte[] buffer = new byte[4096];
                    long restante = tamanho;
                    while (restante > 0) {
                        int ler = (int) Math.min(buffer.length, restante);
                        int lidos = dis.read(buffer, 0, ler);
                        fos.write(buffer, 0, lidos);
                        restante -= lidos;
                    }
                }
            }
        }
        new File(arquivoTemp).delete();
        System.out.println("Restauração concluída!");
    }
}