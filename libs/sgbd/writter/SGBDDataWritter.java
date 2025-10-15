package libs.sgbd.writter;

import libs.dao.GeneralDao;

import java.io.*;
import java.util.Arrays;

/*
    DO NOT USE THIS OUTSIDE OF SGDB CLASS!
*/

public class SGBDDataWritter {

    // TODO: Create logic for "CREATE TABLE"
    public static void createDatabaseSchema(String schema) {

    }

    public static int updateLastIndex(String table) throws IOException {
        int prevIndex = getLastIndex(table);

        RandomAccessFile file = new RandomAccessFile("databases/" + table + ".db", "rw");

        file.seek(0);
        file.writeInt(prevIndex + 1);
        file.getFD().sync();

        return prevIndex + 1;
    }

    public static int getLastIndex(String table) throws IOException {
        InputStream tableFile = new FileInputStream("databases/" + table + ".db");
        DataInputStream dataInputStream = new DataInputStream(tableFile);

        return dataInputStream.readInt();
    }

    public static void writeToTable(String tableName, ByteArrayOutputStream binaryRegister) throws IOException {
        System.out.println("[+] Writting to " + tableName + " the following bytes: " + Arrays.toString(binaryRegister.toByteArray()));

        try (FileOutputStream output = new FileOutputStream("databases/" + tableName + ".db", true)) {
            output.write(binaryRegister.toByteArray());
        }

        updateLastIndex(tableName);
    }
}
