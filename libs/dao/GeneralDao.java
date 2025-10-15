package libs.dao;

import libs.sgbd.SGBD;
import libs.sgbd.types.SGBDTypes;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Replace the tableName attribute logic to annotation logic

abstract public class GeneralDao {
    private final SGBD sgbd = SGBD.getInstance();
    private final static Integer MAX_STRING_SIZE = Short.MAX_VALUE / 2 - 1;
    private final static Short MAX_REGISTER_SIZE = Short.MAX_VALUE;
    protected String tableName;

    protected void insert() {
        try {
            ByteArrayOutputStream data = writeData();

            if (data.size() > MAX_REGISTER_SIZE) {
                System.out.println("[-] Data exceed max size");
                throw new RuntimeException("[-] Data exceed max size");
            }

            ByteArrayOutputStream finalWithHeader = writeHeader(data);

            sgbd.insertAtTable(tableName, finalWithHeader);
        }
        catch (IOException ioException) {
            System.out.println("[-] IOException: " + ioException);
        } catch (NoSuchFieldException noSuchFieldException) {
            System.out.println("[-] Invalid DAO definition (NoSuchField): " + noSuchFieldException);
        } catch (IllegalAccessException illegalAccessException) {
            System.out.println("[-] Invalid DAO definition (IllegalAccess): " + illegalAccessException);
        }
    }

    private ByteArrayOutputStream writeHeader(ByteArrayOutputStream data) throws IOException {
        ByteArrayOutputStream finalWithHeader = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(finalWithHeader);

        // Write register size + 1 (grave)
        dataOutputStream.writeShort(data.size() + 1);

        // Grave
        dataOutputStream.writeBoolean(false);

        // Now write the final data
        finalWithHeader.write(data.toByteArray());

        if (finalWithHeader.size() > MAX_REGISTER_SIZE) {
            System.out.println("[-] Register exceed max size");
            throw new RuntimeException("[-] Register exceed max size");
        }

        return finalWithHeader;
    }

    private ByteArrayOutputStream writeData() throws IOException, NoSuchFieldException, IllegalAccessException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        populateData(dataOutputStream);

        return byteArrayOutputStream;
    }

    private void populateData(DataOutputStream data) throws NoSuchFieldException, IllegalAccessException, IOException {
        ArrayList<String> tableFieldsOrder = sgbd.getSchemasOrder().get(this.tableName);
        HashMap<String, SGBDTypes> tableSchema = sgbd.getSchemas().get(this.tableName);

        for (String field : tableFieldsOrder) {
            SGBDTypes fieldType = tableSchema.get(field);

            Field f = this.getClass().getDeclaredField(field);
            f.setAccessible(true);

            Object fieldValue = f.get(this);

            if (field.equals("id")) {
                fieldValue = sgbd.getLastIndex(this.tableName);
            }

            switch (fieldType) {
                case STRING -> writeString(data, (String) fieldValue);
                case INTEGER -> writeInteger(data, (Integer) fieldValue);
            }
        }
    }

    private void writeString(DataOutputStream binaryRegisterer, String toWrite) throws IOException {
        if (toWrite.length() > MAX_STRING_SIZE) {
            System.out.println("[-] String exceed max size");
            throw new RuntimeException("[-] String exceed max size");
        }

        binaryRegisterer.writeShort(toWrite.length() * 2);
        binaryRegisterer.writeChars(toWrite);
    }

    private void writeInteger(DataOutputStream binaryRegister, Integer toWrite) throws IOException {
        binaryRegister.writeInt(toWrite);
    }

}
