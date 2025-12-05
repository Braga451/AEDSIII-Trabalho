package padroes;

public class KMP {

    // Retorna true se o padrão existir no texto
    public static boolean pesquisar(String texto, String padrao) {
        if (padrao == null || padrao.length() == 0) return false;
        if (texto == null || texto.length() == 0) return false;

        // Converter para array de chars para acesso rápido
        char[] txt = texto.toCharArray();
        char[] pat = padrao.toCharArray();
        
        int N = txt.length;
        int M = pat.length;

        // Cria o array de prefixos (LPS - Longest Prefix Suffix)
        int[] lps = computeLPSArray(pat, M);

        int i = 0; // índice para txt
        int j = 0; // índice para pat

        while ((N - i) >= (M - j)) {
            if (pat[j] == txt[i]) {
                j++;
                i++;
            }
            if (j == M) {
                // Padrão encontrado!
                return true; 
                // j = lps[j - 1]; // Se quiséssemos continuar buscando outras ocorrências
            } else if (i < N && pat[j] != txt[i]) {
                // Não casou
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return false;
    }

    // Pré-processamento do padrão
    private static int[] computeLPSArray(char[] pat, int M) {
        int[] lps = new int[M];
        int len = 0; // Tamanho do prefixo sufixo anterior
        int i = 1;
        lps[0] = 0;

        while (i < M) {
            if (pat[i] == pat[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = len;
                    i++;
                }
            }
        }
        return lps;
    }
}