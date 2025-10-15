package libs.dao;

public class ItemDAO extends GeneralDao{
    @Override
    protected Integer returnPrimaryKey() {
        return 0;
    }

    @Override
    protected void setPrimaryKey(Integer primaryKey) {

    }
}
