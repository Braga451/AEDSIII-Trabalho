package libs.dao.annotations;

public @interface NtoNRelationship {
    String tableName();
    String tableReference();
    boolean doNotResolve() default false;
}
