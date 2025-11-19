package compressao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GerenciadorBackup {

    // ... (Mantenha o método realizarBackup que já existe) ...
    public static void realizarBackup(int tipo) throws IOException {
        // ... (Código anterior de compressão) ...
        File pastaData = new File("data");
        File[] arquivos = pastaData.listFiles((dir, name) -> name.endsWith(".db"));
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("ERRO: Nenhum arquivo .db encontrado.");
            return;
        }
        System.out.println("Iniciando backup de " + arquivos.length + " arquivos...");
        String arquivoUnico = "data/temp_all_files.bin";
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoUnico))) {
            dos.writeInt(arquivos.length);
            for (File f : arquivos) {
                System.out.println(" -> Adicionando: " + f.getName());
                dos.writeUTF(f.getName());
                dos.writeLong(f.length());
                Files.copy(f.toPath(), dos);
            }
        }
        long inicio = System.currentTimeMillis();
        long tamanhoOriginal = new File(arquivoUnico).length();
        String destino = "";
        if (tipo == 1) {
            destino = "backup_huffman.cmp";
            System.out.println("Comprimindo com Huffman...");
            Huffman.comprimir(arquivoUnico, destino);
        } else {
            destino = "backup_lzw.cmp";
            System.out.println("Comprimindo com LZW...");
            LZW.comprimir(arquivoUnico, destino);
        }
        long fim = System.currentTimeMillis();
        long tamanhoFinal = new File(destino).length();
        new File(arquivoUnico).delete();
        
        System.out.println("\n--- RELATÓRIO DE BACKUP ---");
        System.out.println("Arquivo gerado: " + destino);
        System.out.println("Tamanho Original: " + tamanhoOriginal + " bytes");
        System.out.println("Tamanho Comprimido: " + tamanhoFinal + " bytes");
        System.out.printf("Taxa: %.2f%%\n", 100.0 * (1.0 - ((double)tamanhoFinal / tamanhoOriginal)));
        System.out.println("---------------------------");
    }

    // --- NOVO MÉTODO: RESTAURAR BACKUP ---
    public static void restaurarBackup(int tipo) throws IOException {
        String arquivoBackup = (tipo == 1) ? "backup_huffman.cmp" : "backup_lzw.cmp";
        File fBackup = new File(arquivoBackup);
        
        if (!fBackup.exists()) {
            System.out.println("Erro: Arquivo de backup '" + arquivoBackup + "' não encontrado.");
            return;
        }

        System.out.println("Iniciando restauração de " + arquivoBackup + "...");
        String arquivoTemp = "data/temp_restored.bin";

        // 1. Descomprimir
        if (tipo == 1) {
            Huffman.descomprimir(arquivoBackup, arquivoTemp);
        } else {
            LZW.descomprimir(arquivoBackup, arquivoTemp);
        }

        // 2. Separar os arquivos de volta para a pasta data/
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoTemp))) {
            int qtdArquivos = dis.readInt();
            System.out.println("Restaurando " + qtdArquivos + " arquivos...");

            for (int i = 0; i < qtdArquivos; i++) {
                String nomeArquivo = dis.readUTF();
                long tamanho = dis.readLong();
                
                System.out.println(" -> Restaurando: " + nomeArquivo + " (" + tamanho + " bytes)");
                
                // Cria o arquivo de destino na pasta data
                File destino = new File("data/" + nomeArquivo);
                
                // Copia exatamente 'tamanho' bytes do fluxo para o arquivo
                try (FileOutputStream fos = new FileOutputStream(destino)) {
                    // Buffer para cópia eficiente
                    byte[] buffer = new byte[4096];
                    long bytesRestantes = tamanho;
                    while (bytesRestantes > 0) {
                        int ler = (int) Math.min(buffer.length, bytesRestantes);
                        int lidos = dis.read(buffer, 0, ler);
                        if (lidos == -1) break; // Fim inesperado
                        fos.write(buffer, 0, lidos);
                        bytesRestantes -= lidos;
                    }
                }
            }
        }
        
        // Limpeza
        new File(arquivoTemp).delete();
        System.out.println("Restauração concluída com sucesso! Verifique a pasta 'data'.");
    }
}