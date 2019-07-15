package tk.fishfish.datax.plugin.reader.hivereader;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordSender;
import com.alibaba.datax.common.spi.Reader;
import com.alibaba.datax.common.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Hive 自定义 SQL reader
 *
 * @author 奔波儿灞
 * @since 1.0
 */
public class HiveReader extends Reader {

    /**
     * Job 中的方法仅执行一次，Task 中方法会由框架启动多个 Task 线程并行执行。
     * <p/>
     * 整个 Reader 执行流程是：
     * <pre>
     * Job类init-->prepare-->split
     *
     * Task类init-->prepare-->startRead-->post-->destroy
     * Task类init-->prepare-->startRead-->post-->destroy
     *
     * Job类post-->destroy
     * </pre>
     */
    public static class Job extends Reader.Job {

        private static final Logger LOG = LoggerFactory.getLogger(Job.class);

        private Configuration readerOriginConfig = null;

        @Override
        public void init() {
            LOG.info("init() begin...");
            readerOriginConfig = super.getPluginJobConf();
            validate();
            LOG.info("init() end...");
        }

        private void validate() {
            readerOriginConfig.getNecessaryValue(Key.DEFAULT_FS, HiveReaderErrorCode.DEFAULT_FS_NOT_FIND_ERROR);
            readerOriginConfig.getNecessaryValue(Key.SQL, HiveReaderErrorCode.SQL_NOT_FIND_ERROR);
            // check Kerberos
            Boolean haveKerberos = readerOriginConfig.getBool(Key.HAVE_KERBEROS, false);
            if (haveKerberos) {
                readerOriginConfig.getNecessaryValue(Key.KERBEROS_KEYTAB_FILE_PATH,
                        HiveReaderErrorCode.KERBEROS_KEYTAB_FILE_PATH_NOT_FIND_ERROR);
                readerOriginConfig.getNecessaryValue(Key.KERBEROS_PRINCIPAL,
                        HiveReaderErrorCode.KERBEROS_PRINCIPAL_NOT_FIND_ERROR);
            }
        }

        @Override
        public void prepare() {
        }

        @Override
        public List<Configuration> split(int adviceNumber) {
            LOG.warn("split() unsupported...");
            return Collections.singletonList(readerOriginConfig);
        }

        @Override
        public void post() {
        }

        @Override
        public void destroy() {
        }

    }

    public static class Task extends Reader.Task {

        private static final Logger LOG = LoggerFactory.getLogger(Reader.Task.class);

        private static final String DOUBLE_QUOTATION  = "\"";

        private Configuration taskConfig = null;
        private String sql = null;
        private String tmpTableName = null;
        private String tmpPath = null;
        private DFSUtil dfsUtil = null;
        private Set<String> sourceFiles = null;

        @Override
        public void init() {
            LOG.info("init() begin...");
            taskConfig = super.getPluginJobConf();
            sql = taskConfig.getString(Key.SQL);
            tmpTableName = getTmpTableName();
            tmpPath = getTmpPath(tmpTableName);
            dfsUtil = new DFSUtil(taskConfig);
            LOG.info("init() end...");
        }

        private String getTmpTableName() {
            return UUID.randomUUID().toString().replace("-", "");
        }

        private String getTmpPath(String tmpTableName) {
            return Key.TMP_PATH_PREFIX + tmpTableName;
        }

        @Override
        public void prepare() {
            LOG.info("prepare() begin...");
            String hiveCmd = "CREATE TABLE " + tmpTableName + " STORED AS ORCFILE LOCATION '" + tmpPath + "' AS " + sql;
            LOG.info("prepare() hive cmd: {}", hiveCmd);
            try {
                if (!ShellUtil.exec(new String[]{"hive", "-e", DOUBLE_QUOTATION + hiveCmd + DOUBLE_QUOTATION})) {
                    throw DataXException.asDataXException(HiveReaderErrorCode.SHELL_ERROR, "创建hive临时表脚本执行失败");
                }
            } catch (Exception e) {
                throw DataXException.asDataXException(HiveReaderErrorCode.SHELL_ERROR, "创建hive临时表脚本执行失败", e);
            }
            sourceFiles = dfsUtil.getAllFiles(Collections.singletonList(tmpPath), Constant.ORC);
            LOG.info("prepare() end...");
        }

        @Override
        public void startRead(RecordSender recordSender) {
            LOG.info("startRead() begin...");
            for (String sourceFile : sourceFiles) {
                LOG.info("reading file: {}", sourceFile);
                dfsUtil.orcFileStartRead(sourceFile, this.taskConfig, recordSender, this.getTaskPluginCollector());
                if (recordSender != null) {
                    recordSender.flush();
                }
            }
            LOG.info("startRead() end...");
        }

        @Override
        public void post() {
            LOG.info("post() begin...");
            String hiveCmd = "drop table " + tmpTableName;
            LOG.info("post() hive cmd: {}", hiveCmd);
            // 执行脚本，删除临时表
            try {
                if (!ShellUtil.exec(new String[]{"hive", "-e", DOUBLE_QUOTATION + hiveCmd + DOUBLE_QUOTATION})) {
                    throw DataXException.asDataXException(HiveReaderErrorCode.SHELL_ERROR, "删除hive临时表脚本执行失败");
                }
            } catch (Exception e) {
                throw DataXException.asDataXException(HiveReaderErrorCode.SHELL_ERROR, "删除hive临时表脚本执行失败", e);
            }
            LOG.info("post() end...");
        }

        @Override
        public void destroy() {
        }

    }

}
