package indices;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class HashExtensivel {

    private String diretorioFile;
    private String cestosFile;
    private RandomAccessFile rafDiretorio;
    private RandomAccessFile rafCestos;
    private int profundidadeGlobal;
    private long[] diretorio;

    /**
     * Construtor da classe de Hash Extensível.
     * @param nomeBase O nome base para os arquivos de índice (ex: "categorias_pk").
     * @throws IOException
     */
    public HashExtensivel(String nomeBase) throws IOException {
        this.diretorioFile = "data/" + nomeBase + "_dir.db";
        this.cestosFile = "data/" + nomeBase + "_cestos.db";
        this.rafDiretorio = new RandomAccessFile(diretorioFile, "rw");
        this.rafCestos = new RandomAccessFile(cestosFile, "rw");

        // Se o arquivo de diretório é novo, inicializa o Hash
        if (rafDiretorio.length() == 0) {
            // Estado inicial: profundidade global = 1
            this.profundidadeGlobal = 1;
            rafDiretorio.writeInt(this.profundidadeGlobal);

            // Cria os dois primeiros cestos, ambos com profundidade local 1
            Cesto cesto0 = new Cesto(1);
            Cesto cesto1 = new Cesto(1);

            // Escreve os cestos no arquivo de cestos
            long posCesto0 = rafCestos.length();
            rafCestos.seek(posCesto0);
            rafCestos.write(cesto0.toByteArray());

            long posCesto1 = rafCestos.length();
            rafCestos.seek(posCesto1);
            rafCestos.write(cesto1.toByteArray());

            // Inicializa o diretório em memória
            this.diretorio = new long[2]; // Tamanho do diretório = 2^pGlobal
            this.diretorio[0] = posCesto0;
            this.diretorio[1] = posCesto1;

            // Escreve os ponteiros do diretório no arquivo
            rafDiretorio.writeLong(this.diretorio[0]);
            rafDiretorio.writeLong(this.diretorio[1]);

        } else {
            // Se o arquivo já existe, carrega o estado
            this.profundidadeGlobal = rafDiretorio.readInt();
            int tamanhoDiretorio = 1 << this.profundidadeGlobal; // 2^pGlobal
            this.diretorio = new long[tamanhoDiretorio];
            for (int i = 0; i < tamanhoDiretorio; i++) {
                this.diretorio[i] = rafDiretorio.readLong();
            }
        }
    }

    /**
     * Função hash. Calcula o índice do diretório para uma dada chave.
     * Usa os 'p' bits menos significativos da chave.
     * @param chave O ID do registro.
     * @return O índice no diretório.
     */
    private int hash(int chave) {
        return chave % (1 << profundidadeGlobal);
    }
    
    /**
     * Função hash secundária para redistribuição de chaves durante o split.
     * @param chave A chave a ser hasheada.
     * @param p A profundidade a ser usada no cálculo.
     * @return O hash da chave.
     */
    private int hash(int chave, int p) {
        return chave % (1 << p);
    }


    public long create(int chave, long endereco) throws IOException {
        int indiceDiretorio = hash(chave);
        long posCesto = diretorio[indiceDiretorio];

        rafCestos.seek(posCesto);
        byte[] cestoBytes = new byte[Cesto.TAMANHO_CESTO * (Integer.BYTES + Long.BYTES) + Integer.BYTES * 2];
        rafCestos.read(cestoBytes);
        Cesto cesto = new Cesto(0);
        cesto.fromByteArray(cestoBytes);

        // Se o cesto não está cheio, insere e reescreve no arquivo
        if (!cesto.isFull()) {
            cesto.create(chave, endereco);
            rafCestos.seek(posCesto);
            rafCestos.write(cesto.toByteArray());
            return posCesto;
        }

        // Se o cesto está CHEIO, precisamos dividi-lo (split)
        
        int pLocal = cesto.getProfundidadeLocal();
        
        // CASO 1: A profundidade local é menor que a global.
        // O diretório não precisa ser duplicado.
        if (pLocal < profundidadeGlobal) {
            dividirCesto(indiceDiretorio, cesto, posCesto);

            // Tenta inserir a chave novamente após o split
            return create(chave, endereco);
        }
        
        // CASO 2: A profundidade local é igual à global.
        // O diretório PRECISA ser duplicado.
        if (pLocal == profundidadeGlobal) {
            duplicarDiretorio();
            dividirCesto(indiceDiretorio, cesto, posCesto);
            
            // Tenta inserir a chave novamente após duplicar e dividir
            return create(chave, endereco);
        }
        
        return -1; // Situação de erro
    }
    
    private void duplicarDiretorio() throws IOException {
        profundidadeGlobal++;
        int tamanhoAntigo = diretorio.length;
        int tamanhoNovo = tamanhoAntigo * 2;
        
        long[] novoDiretorio = new long[tamanhoNovo];
        // Copia os ponteiros antigos para o novo diretório
        for (int i = 0; i < tamanhoAntigo; i++) {
            novoDiretorio[i] = diretorio[i];
            novoDiretorio[i + tamanhoAntigo] = diretorio[i];
        }
        diretorio = novoDiretorio;
        
        // Persiste o novo diretório em disco
        rafDiretorio.seek(0);
        rafDiretorio.writeInt(profundidadeGlobal);
        for (long pos : diretorio) {
            rafDiretorio.writeLong(pos);
        }
    }
    
    private void dividirCesto(int indiceOriginal, Cesto cestoAntigo, long posCestoAntigo) throws IOException {
        // Incrementa a profundidade local do cesto antigo
        cestoAntigo.setProfundidadeLocal(cestoAntigo.getProfundidadeLocal() + 1);
        int pLocalNova = cestoAntigo.getProfundidadeLocal();
        
        // Cria um novo cesto "irmão"
        Cesto cestoNovo = new Cesto(pLocalNova);
        
        // Pega todos os pares [chave, endereço] do cesto antigo para redistribuir
        List<Integer> chavesAntigas = new ArrayList<>();
        List<Long> enderecosAntigos = new ArrayList<>();
        for (int i = 0; i < cestoAntigo.getQuantidade(); i++) {
            chavesAntigas.add(cestoAntigo.getChaves()[i]);
            enderecosAntigos.add(cestoAntigo.getEnderecos()[i]);
        }
        
        // Limpa o cesto antigo para reinserir os elementos
        Cesto cestoAntigoLimpo = new Cesto(pLocalNova);

        // Redistribui as chaves
        for (int i = 0; i < chavesAntigas.size(); i++) {
            int chave = chavesAntigas.get(i);
            long endereco = enderecosAntigos.get(i);
            int novoIndice = hash(chave, pLocalNova);

            // Decide se a chave fica no cesto antigo ou vai para o novo
            if(hash(chave, profundidadeGlobal) == indiceOriginal){
                cestoAntigoLimpo.create(chave, endereco);
            } else {
                cestoNovo.create(chave, endereco);
            }
        }
        
        // Persiste o cesto antigo atualizado
        rafCestos.seek(posCestoAntigo);
        rafCestos.write(cestoAntigoLimpo.toByteArray());
        
        // Persiste o novo cesto no final do arquivo
        long posCestoNovo = rafCestos.length();
        rafCestos.seek(posCestoNovo);
        rafCestos.write(cestoNovo.toByteArray());
        
        // Atualiza os ponteiros no diretório
        for (int i = 0; i < diretorio.length; i++) {
            if (diretorio[i] == posCestoAntigo) {
                 if ( (i >> (pLocalNova-1)) % 2 == 1 ){ // Usa bit de decisão
                    diretorio[i] = posCestoNovo;
                }
            }
        }
        
        // Persiste o diretório atualizado
        rafDiretorio.seek(4); // Pula a profundidade global
        for (long pos : diretorio) {
            rafDiretorio.writeLong(pos);
        }
    }

    public long read(int chave) throws IOException {
        int indiceDiretorio = hash(chave);
        long posCesto = diretorio[indiceDiretorio];

        rafCestos.seek(posCesto);
        byte[] cestoBytes = new byte[Cesto.TAMANHO_CESTO * (Integer.BYTES + Long.BYTES) + Integer.BYTES * 2];
        rafCestos.read(cestoBytes);
        Cesto cesto = new Cesto(0);
        cesto.fromByteArray(cestoBytes);

        return cesto.read(chave);
    }

    public boolean update(int chave, long novoEndereco) throws IOException {
        int indiceDiretorio = hash(chave);
        long posCesto = diretorio[indiceDiretorio];

        rafCestos.seek(posCesto);
        byte[] cestoBytes = new byte[Cesto.TAMANHO_CESTO * (Integer.BYTES + Long.BYTES) + Integer.BYTES * 2];
        rafCestos.read(cestoBytes);
        Cesto cesto = new Cesto(0);
        cesto.fromByteArray(cestoBytes);

        if (cesto.update(chave, novoEndereco)) {
            rafCestos.seek(posCesto);
            rafCestos.write(cesto.toByteArray());
            return true;
        }
        return false;
    }

    public boolean delete(int chave) throws IOException {
        int indiceDiretorio = hash(chave);
        long posCesto = diretorio[indiceDiretorio];

        rafCestos.seek(posCesto);
        byte[] cestoBytes = new byte[Cesto.TAMANHO_CESTO * (Integer.BYTES + Long.BYTES) + Integer.BYTES * 2];
        rafCestos.read(cestoBytes);
        Cesto cesto = new Cesto(0);
        cesto.fromByteArray(cestoBytes);

        if (cesto.delete(chave)) {
            rafCestos.seek(posCesto);
            rafCestos.write(cesto.toByteArray());
            return true;
        }
        return false;
        // NOTA: A fusão de cestos vazios não está implementada para simplificação.
    }
    
    public void close() throws IOException {
        rafDiretorio.close();
        rafCestos.close();
    }
}