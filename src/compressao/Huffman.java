package compressao;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {

    static class Node implements Comparable<Node> {
        Byte data;
        int frequency;
        Node left, right;

        Node(Byte data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }

        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }

    public static void comprimir(String src, String dst) throws IOException {
        // ... (Mantenha seu código de comprimir aqui, igual ao anterior) ...
        // Vou colar apenas a lógica de descompressão abaixo para economizar espaço, 
        // mas mantenha o método comprimir!
        byte[] data = Files.readAllBytes(new File(src).toPath());
        Map<Byte, Integer> freq = new HashMap<>();
        for (byte b : data) freq.put(b, freq.getOrDefault(b, 0) + 1);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (var entry : freq.entrySet()) pq.add(new Node(entry.getKey(), entry.getValue()));

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        
        if (pq.isEmpty()) return;
        Node root = pq.poll();

        Map<Byte, String> huffmanCodes = new HashMap<>();
        buildCode(root, "", huffmanCodes);

        StringBuilder sb = new StringBuilder();
        for (byte b : data) sb.append(huffmanCodes.get(b));

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(dst))) {
            dos.writeInt(freq.size());
            for (var entry : freq.entrySet()) {
                dos.writeByte(entry.getKey());
                dos.writeInt(entry.getValue());
            }
            dos.writeInt(sb.length());

            int index = 0;
            while (index < sb.length()) {
                int b = 0;
                for (int i = 0; i < 8; i++) {
                    if (index < sb.length()) {
                        if (sb.charAt(index) == '1') b |= (1 << (7 - i));
                        index++;
                    }
                }
                dos.writeByte(b);
            }
        }
    }

    private static void buildCode(Node root, String s, Map<Byte, String> codes) {
        if (root == null) return;
        if (root.left == null && root.right == null) codes.put(root.data, s.length() > 0 ? s : "1");
        buildCode(root.left, s + "0", codes);
        buildCode(root.right, s + "1", codes);
    }
    
    // --- NOVO MÉTODO: DESCOMPRIMIR ---
    public static void descomprimir(String src, String dst) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(src));
             FileOutputStream fos = new FileOutputStream(dst)) {
            
            // 1. Ler Tabela de Frequência
            int tableSize = dis.readInt();
            Map<Byte, Integer> freq = new HashMap<>();
            for (int i = 0; i < tableSize; i++) {
                byte b = dis.readByte();
                int f = dis.readInt();
                freq.put(b, f);
            }

            // 2. Reconstruir a Árvore
            PriorityQueue<Node> pq = new PriorityQueue<>();
            for (var entry : freq.entrySet()) pq.add(new Node(entry.getKey(), entry.getValue()));

            while (pq.size() > 1) {
                Node left = pq.poll();
                Node right = pq.poll();
                Node parent = new Node(null, left.frequency + right.frequency);
                parent.left = left;
                parent.right = right;
                pq.add(parent);
            }
            Node root = pq.poll();

            // 3. Ler os bits e navegar na árvore
            int totalBits = dis.readInt();
            Node current = root;
            int bitsRead = 0;
            
            while (bitsRead < totalBits) {
                int b = dis.readUnsignedByte(); // Lê 1 byte do arquivo (8 bits)
                
                for (int i = 0; i < 8 && bitsRead < totalBits; i++) {
                    // Verifica o bit na posição i
                    boolean isOne = ((b >> (7 - i)) & 1) == 1;
                    
                    if (isOne) {
                        current = current.right;
                    } else {
                        current = current.left;
                    }

                    // Se chegou na folha, escreve o byte e volta pra raiz
                    if (current.left == null && current.right == null) {
                        fos.write(current.data);
                        current = root;
                    }
                    bitsRead++;
                }
            }
        }
    }
}