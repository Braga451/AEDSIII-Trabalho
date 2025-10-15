package libs.sgbd;

import libs.sgbd.types.SGBDTypes;
import libs.sgbd.writter.SGBDDataWritter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

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
        System.out.println("[+] Started SGBD");

        this.schemas = new HashMap<>();
        this.schemasOrder = new HashMap<>();

        this.createFolders();
        System.out.println("[+] Populating schemas...");
        this.loadSchemas();
        System.out.println("[+] Schemas populated");
        this.createTables();
        System.out.println("[+] Tables created");
    }

    // TODO: Return a clone instead of raw object
    public HashMap<String, HashMap<String, SGBDTypes>> getSchemas() {
        return this.schemas;
    }

    // TODO: Return a clone instead of raw object
    public HashMap<String, ArrayList<String>> getSchemasOrder() {
        return this.schemasOrder;
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

    // TODO: Create a properly schema with less space.
    private void loadSchemas() {
        File schemasFolder = new File("schemas");

        File[] schemas = schemasFolder.listFiles();

        if (Objects.nonNull(schemas)) {
            for (File schema : schemas) {
                String fileName = schema.getName();

                if (fileName.endsWith(".schema")) {
                    String tableName = fileName.split("\\.")[0];

                    this.loadSchema(tableName, schema);
                }
            }
        }

        System.out.println(this.schemas);
    }


    private void loadSchema(String tableName, File schema) {
        try {
            Scanner scanner = new Scanner(schema);

            HashMap<String, SGBDTypes> tableSchema = new HashMap<>();
            this.schemas.put(tableName, tableSchema);

            ArrayList<String> tableSchemaOrder = new ArrayList<>();
            this.schemasOrder.put(tableName, tableSchemaOrder);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] fieldNameType = line.split(",");

                String stringType = fieldNameType[0];
                String fieldName = fieldNameType[1];

                SGBDTypes fieldType = SGBDTypes.valueOf(stringType);

                tableSchema.put(fieldName, fieldType);
                tableSchemaOrder.add(fieldName);
            }
        } catch (FileNotFoundException e) {
            System.out.println("[-] File not found");
        }
    }

    private void createTables()  {
        for (String s : this.schemas.keySet()) {
            File table = new File("databases/" + s + ".db");
            try {
                if (table.createNewFile()) {
                    System.out.println("[+] Table " + s + " created");

                    FileOutputStream fileOutputStream = new FileOutputStream(table);
                    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

                    dataOutputStream.writeInt(0);

                    fileOutputStream.close();
                } else {
                    System.out.println("[+] Table " + s + " already exists");
                }
            }
            catch (IOException ioException) {
                System.out.println("[-] Invalid table: " + s + " | error: " + ioException);
            }
        }
    }

    public void insertAtTable(String tableName, ByteArrayOutputStream binaryRegister) throws IOException {
        SGBDDataWritter.writeToTable(tableName, binaryRegister);
    }

    public int getLastIndex(String tableName) {
        try {
            return SGBDDataWritter.getLastIndex(tableName);
        }
        catch (FileNotFoundException fileNotFoundException) {
            System.out.println("[-] Table not found: " + fileNotFoundException);
            return -1;
        }
        catch (IOException ioException) {
            System.out.println("[-] Invalid table format: " + ioException);
            return -1;
        }
    }


    public long getTableSize(String tableName) {
        File f = new File("databases/" + tableName + ".db");

        return f.length();
    }
}
