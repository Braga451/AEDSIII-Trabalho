package seguranca;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class CriptografiaRSA {

    private static final String ALGORITHM = "RSA";
    private static final String DIR_CHAVES = "data/chaves";
    private static final String PATH_CHAVE_PUBLICA = DIR_CHAVES + "/public.key";
    private static final String PATH_CHAVE_PRIVADA = DIR_CHAVES + "/private.key";

    public CriptografiaRSA() {
        try {
            File diretorio = new File(DIR_CHAVES);
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }
            // Se as chaves não existem, gera agora!
            if (!new File(PATH_CHAVE_PUBLICA).exists() || !new File(PATH_CHAVE_PRIVADA).exists()) {
                System.out.println("Chaves RSA não encontradas. Gerando novo par...");
                gerarParDeChaves();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gerarParDeChaves() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        // Garante que o diretório existe antes de salvar
        new File(DIR_CHAVES).mkdirs();

        try (FileOutputStream fos = new FileOutputStream(PATH_CHAVE_PUBLICA)) {
            fos.write(pair.getPublic().getEncoded());
        }

        try (FileOutputStream fos = new FileOutputStream(PATH_CHAVE_PRIVADA)) {
            fos.write(pair.getPrivate().getEncoded());
        }
    }

    public PublicKey carregarChavePublica() throws Exception {
        // Dupla verificação: se o arquivo sumiu, gera de novo antes de ler
        if (!new File(PATH_CHAVE_PUBLICA).exists()) {
            gerarParDeChaves();
        }
        byte[] keyBytes = Files.readAllBytes(new File(PATH_CHAVE_PUBLICA).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePublic(spec);
    }

    public PrivateKey carregarChavePrivada() throws Exception {
        // Dupla verificação
        if (!new File(PATH_CHAVE_PRIVADA).exists()) {
            gerarParDeChaves();
        }
        byte[] keyBytes = Files.readAllBytes(new File(PATH_CHAVE_PRIVADA).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(spec);
    }

    public String criptografar(String textoClaro) throws Exception {
        PublicKey publicKey = carregarChavePublica();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(textoClaro.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String descriptografar(String textoCriptografadoBase64) throws Exception {
        PrivateKey privateKey = carregarChavePrivada();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(textoCriptografadoBase64);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}