package tk.fishfish.datax.plugin.reader.hivereader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Shell 工具累
 *
 * @author dalizu on 2018/11/10.
 * @version v1.0
 */
public class ShellUtil {

    private static final int SUCCESS = 0;
    private static final Logger LOG = LoggerFactory.getLogger(ShellUtil.class);

    public static boolean exec(String[] command) throws Exception {
        try {
            Process process = Runtime.getRuntime().exec(command);
            read(process.getInputStream());
            StringBuilder errMsg = read(process.getErrorStream());
            // 等待程序执行结束并输出状态
            int exitCode = process.waitFor();
            if (exitCode == SUCCESS) {
                LOG.info("脚本执行成功");
                return true;
            } else {
                LOG.info("脚本执行失败: {}" + errMsg.toString());
                return false;
            }
        } catch (Exception e) {
            LOG.error("脚本执行失败", e);
            throw e;
        }
    }

    private static StringBuilder read(InputStream inputStream) throws IOException {
        StringBuilder resultMsg = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                resultMsg.append(line);
                resultMsg.append("\r\n");
            }
            return resultMsg;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOG.error("close inputStream error", e);
                }
            }
        }
    }
}
