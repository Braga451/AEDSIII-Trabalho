package libs.sgbd.writter;

import libs.dao.GeneralDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/*
    DO NOT USE THIS OUTSIDE OF SGDB CLASS!
 */

public class SGBDDataWritter {
    public static void createDatabaseSchema() {

    }

    public static void writeToDatabase(String database, ByteArrayOutputStream binaryRegister) throws IOException {
        System.out.println("[+] Writting to " + database + " the following bytes: " + Arrays.toString(binaryRegister.toByteArray()));
    }
}
