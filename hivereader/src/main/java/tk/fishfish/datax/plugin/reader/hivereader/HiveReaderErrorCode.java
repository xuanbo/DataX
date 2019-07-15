package tk.fishfish.datax.plugin.reader.hivereader;

import com.alibaba.datax.common.spi.ErrorCode;

/**
 * Hive 错误码
 *
 * @author 奔波儿灞
 * @since 1.0
 */
public enum HiveReaderErrorCode implements ErrorCode {

    /**
     * defaultFS配置未找到
     */
    DEFAULT_FS_NOT_FIND_ERROR("HiveReader-01", "您未配置defaultFS值"),

    /**
     * SQL配置未找到
     */
    SQL_NOT_FIND_ERROR("HiveReader-02", "您未配置sql值"),

    /**
     * kerberosKeytabFilePath未找到
     */
    KERBEROS_KEYTAB_FILE_PATH_NOT_FIND_ERROR("HiveReader-03", "您未配置kerberosKeytabFilePath值"),

    /**
     * kerberosPrincipal未找到
     */
    KERBEROS_PRINCIPAL_NOT_FIND_ERROR("HiveReader-04", "您未配置kerberosPrincipal值"),

    /**
     * kerberos
     */
    KERBEROS_LOGIN_ERROR("HiveReader-05", "kerberos认证失败"),

    /**
     * path格式有误
     */
    PATH_CONFIG_ERROR("HiveReader-06", "您配置的path格式有误"),

    /**
     * 读取文件出错
     */
    READ_FILE_ERROR("HiveReader-07", "读取文件出错"),

    /**
     * 参数配置错误
     */
    CONFIG_INVALID_EXCEPTION("HiveReader-08", "参数配置错误"),

    /**
     * 执行shell失败
     */
    SHELL_ERROR("HiveReader-09", "执行shell失败"),

    /**
     * 文件类型目前不支持
     */
    FILE_TYPE_UNSUPPORT("HiveReader-10", "文件类型目前不支持"),

    /**
     * 读取SequenceFile文件出错
     */
    READ_SEQUENCEFILE_ERROR("HiveReader-11", "读取SequenceFile文件出错"),

    /**
     * 读取RCFile文件出错
     */
    READ_RCFILE_ERROR("HiveReader-12", "读取RCFile文件出错"),

    /**
     * 您配置的值不合法
     */
    BAD_CONFIG_VALUE("HiveReader-13", "您配置的值不合法"),

    ;

    private final String code;
    private final String description;

    HiveReaderErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Description:[%s]. ", this.code, this.description);
    }
}