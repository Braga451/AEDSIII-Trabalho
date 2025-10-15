package libs.sgbd;

import libs.sgbd.types.SGBDTypes;
import libs.sgbd.writter.SGBDDataWritter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SGBD {
    private static SGBD INSTANCE;
    private final HashMap<String, HashMap<String, SGBDTypes>> schemas;
    private final HashMap<String, ArrayList<String>> schemasOrder;

    public static SGBD getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new SGBD();
        }

        return INSTANCE;
    }

    private SGBD() {
        this.schemas = new HashMap<>();
        this.schemasOrder = new HashMap<>();

        createFolders();
    }

    private void createFolders() {
        File databasesFolder = new File("databases");
        File schemasFolder = new File("schemas");

        if (!databasesFolder.exists()) {
            boolean isDatabasesFolderCreated = databasesFolder.mkdir();

            if (isDatabasesFolderCreated) {
                System.out.println("[+] Databases folder created");
            }
        }
        else {
            System.out.println("[+] Already have databases folder");
        }

        if (!schemasFolder.exists()) {
            boolean isSchemasFolderCreated = schemasFolder.mkdir();

            if (isSchemasFolderCreated) {
                System.out.println("[+] Schemas folder created");
            }
        }
        else {
            System.out.println("[+] Already have schemas folder");
        }
    }

    private void loadSchemas() {

    }

    public void insertAtTable(String tablename, ByteArrayOutputStream binaryRegister) throws IOException {
        SGBDDataWritter.writeToDatabase(tablename, binaryRegister);
    }

    public boolean verifyIfTableExists(String tableName) {
        return this.schemas.containsKey(tableName);
    }
}
