package padroes;

public class BoyerMoore {

    private static final int NO_OF_CHARS = 256; // Tabela ASCII estendida

    // Retorna true se o padrão existir no texto
    public static boolean pesquisar(String texto, String padrao) {
        if (padrao == null || padrao.length() == 0) return false;
        if (texto == null || texto.length() == 0) return false;

        char[] txt = texto.toCharArray();
        char[] pat = padrao.toCharArray();
        
        int m = pat.length;
        int n = txt.length;

        int[] badChar = new int[NO_OF_CHARS];

        // Preenche a tabela de caracteres ruins
        badCharHeuristic(pat, m, badChar);

        int s = 0; // s é o deslocamento do padrão em relação ao texto
        while (s <= (n - m)) {
            int j = m - 1;

            // Varre da direita para a esquerda
            while (j >= 0 && pat[j] == txt[s + j])
                j--;

            if (j < 0) {
                // Padrão encontrado!
                return true;
                // s += (s + m < n) ? m - badChar[txt[s + m]] : 1; // Para continuar buscando
            } else {
                // Desloca o padrão de acordo com a tabela badChar
                s += Math.max(1, j - badChar[txt[s + j]]);
            }
        }
        return false;
    }

    // Pré-processamento: Bad Character Heuristic
    private static void badCharHeuristic(char[] str, int size, int[] badChar) {
        // Inicializa todas as ocorrências como -1
        for (int i = 0; i < NO_OF_CHARS; i++)
            badChar[i] = -1;

        // Preenche o valor da última ocorrência de cada caractere
        for (int i = 0; i < size; i++)
            badChar[(int) str[i]] = i;
    }
}