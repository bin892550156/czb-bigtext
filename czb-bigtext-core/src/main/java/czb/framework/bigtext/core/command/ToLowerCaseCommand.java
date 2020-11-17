package czb.framework.bigtext.core.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * 将该文件的文本内容的所有字母转换成小写字母 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 * @see String#toLowerCase(Locale)
 */
public class ToLowerCaseCommand extends WriterCommand {

    /**
     * 请查阅 {@link String#toLowerCase(Locale)}
     */
    private Locale locale;

    /**
     * 新建一个 {@link ToLowerCaseCommand} 实例
     * @param tempFile 要写入的临时文件
     * @param locale 请查阅 {@link String#toLowerCase(Locale)}
     * @throws FileNotFoundException
     */
    public ToLowerCaseCommand(File tempFile,Locale locale) throws FileNotFoundException {
        super(tempFile);
        this.locale=locale;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        String segStr=new String(segment).toLowerCase(locale);
        writeData(segStr);
        return false;
    }
}
