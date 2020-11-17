package czb.framework.bigtext.core.command;

import java.io.File;
import java.io.IOException;

/**
 * 修剪该文件文本内容的开头和结尾，将开头和接口的空格，回车换行去掉 的命令
 * @author chenzhuobin
 * @since 2020/11/17 0017
 */
public class TrimNoCRLFCommand extends TrimCommand{

    /**
     * 新建一个 {@link TrimNoCRLFCommand} 实例
     * @param srcfile 源文件
     * @param tempFile 要写入的临时文件
     * @param srcFileLength 源文件的文本内容总字符大小
     * @throws IOException 当 tempFile 不存在时抛出
     */
    public TrimNoCRLFCommand(File srcfile, File tempFile, long srcFileLength) throws IOException {
        super(srcfile, tempFile, srcFileLength);
    }

    protected boolean isTrminChar(char c){
        return c==' '||c=='\r'||c=='\n';
    }
}
