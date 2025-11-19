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
    private static final String PATH_CHAVE_PUBLICA = "data/chaves/public.key";
    private static final String PATH_CHAVE_PRIVADA = "data/chaves/private.key";

    public CriptografiaRSA() {
        try {
            File diretorio = new File("data/chaves");
            if (!diretorio.exists()) {
                diretorio.mkdirs();
                gerarParDeChaves();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gerarParDeChaves() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048); // Tamanho da chave: 2048 bits (seguro)
        KeyPair pair = keyGen.generateKeyPair();

        // Salva Chave Pública
        try (FileOutputStream fos = new FileOutputStream(PATH_CHAVE_PUBLICA)) {
            fos.write(pair.getPublic().getEncoded());
        }

        // Salva Chave Privada
        try (FileOutputStream fos = new FileOutputStream(PATH_CHAVE_PRIVADA)) {
            fos.write(pair.getPrivate().getEncoded());
        }
        System.out.println("Novas chaves RSA geradas em data/chaves/");
    }

    public PublicKey carregarChavePublica() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(PATH_CHAVE_PUBLICA).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePublic(spec);
    }

    public PrivateKey carregarChavePrivada() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(PATH_CHAVE_PRIVADA).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(spec);
    }

    // Criptografa e retorna em Base64 para facilitar o armazenamento como String
    public String criptografar(String textoClaro) throws Exception {
        PublicKey publicKey = carregarChavePublica();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(textoClaro.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Recebe Base64, descriptografa e retorna String
    public String descriptografar(String textoCriptografadoBase64) throws Exception {
        PrivateKey privateKey = carregarChavePrivada();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(textoCriptografadoBase64);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}