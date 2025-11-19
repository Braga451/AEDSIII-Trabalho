package compressao;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class LZW {

    public static void comprimir(String src, String dst) throws IOException {
        // ... (Mantenha o método comprimir igual ao que você já tem) ...
        byte[] data = Files.readAllBytes(new File(src).toPath());
        Map<List<Byte>, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            List<Byte> list = new ArrayList<>();
            list.add((byte) i);
            dictionary.put(list, i);
        }
        List<Byte> w = new ArrayList<>();
        List<Integer> compressed = new ArrayList<>();
        int dictSize = 256;
        for (byte b : data) {
            List<Byte> wc = new ArrayList<>(w);
            wc.add(b);
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
                compressed.add(dictionary.get(w));
                dictionary.put(wc, dictSize++);
                w = new ArrayList<>();
                w.add(b);
            }
        }
        if (!w.isEmpty()) compressed.add(dictionary.get(w));
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(dst))) {
            for (int code : compressed) dos.writeInt(code);
        }
    }

    // --- NOVO MÉTODO: DESCOMPRIMIR ---
    public static void descomprimir(String src, String dst) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(src));
             FileOutputStream fos = new FileOutputStream(dst)) {

            // 1. Inicializa dicionário inverso (Int -> List<Byte>)
            Map<Integer, List<Byte>> dictionary = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                List<Byte> list = new ArrayList<>();
                list.add((byte) i);
                dictionary.put(i, list);
            }
            int dictSize = 256;

            // 2. Lê o primeiro código
            if (dis.available() == 0) return;
            int oldCode = dis.readInt();
            List<Byte> w = dictionary.get(oldCode);
            
            // Escreve w no arquivo
            for (byte b : w) fos.write(b);

            // 3. Loop principal
            while (dis.available() > 0) {
                int newCode = dis.readInt();
                List<Byte> entry;
                
                if (dictionary.containsKey(newCode)) {
                    entry = dictionary.get(newCode);
                } else if (newCode == dictSize) {
                    // Caso especial: cScSc
                    entry = new ArrayList<>(w);
                    entry.add(w.get(0));
                } else {
                    throw new IOException("LZW descomprimir: Código inválido.");
                }

                // Escreve a sequência encontrada
                for (byte b : entry) fos.write(b);

                // Adiciona nova entrada ao dicionário
                List<Byte> newEntry = new ArrayList<>(w);
                newEntry.add(entry.get(0));
                dictionary.put(dictSize++, newEntry);

                w = entry;
            }
        }
    }
}