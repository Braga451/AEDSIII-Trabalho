package libs.dao;

import libs.sgbd.SGBD;
import libs.sgbd.writter.SGBDDataWritter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

abstract public class GeneralDao {
    private final SGBD sgbd = SGBD.getInstance();

    protected void insert() {
        try {
            String[] className = this.getClass().getCanonicalName().split("\\.");
            String table = className[className.length - 1];

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            // TODO: Populate

            byte[] totalRegisterSize = {Integer.valueOf(dataOutputStream.size() + 1).byteValue()}; // + 1 because of grave
            dataOutputStream.write(totalRegisterSize, 0, 1);

            byte[] grave = {0};
            dataOutputStream.write(grave, 0, 1);

            sgbd.insertAtTable(table, byteArrayOutputStream);
        }
        catch (IOException ioException) {
            System.out.println("[-] IOException");
        }
    }


}
