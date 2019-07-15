package tk.fishfish.datax.plugin.reader.hivereader;

/**
 * Hive 配置 Key
 *
 * @author 奔波儿灞
 * @since 1.0
 */
public final class Key {

    /**
     * 此处声明插件用到的需要插件使用者提供的配置项
     */
    public final static String DEFAULT_FS = "defaultFS";
    public final static String SQL = "sql";
    public static final String HADOOP_CONFIG = "hadoopConfig";
    public static final String HAVE_KERBEROS = "haveKerberos";
    public static final String KERBEROS_KEYTAB_FILE_PATH = "kerberosKeytabFilePath";
    public static final String KERBEROS_PRINCIPAL = "kerberosPrincipal";

    /**
     * 此处声明插件常量
     */
    public static final String TMP_PATH_PREFIX = "/tmp/datax-hivereader/";
}
