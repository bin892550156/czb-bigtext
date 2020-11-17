package czb.framework.bigtext.core.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * 将该文件的文本内容的所有字母转换成大写字母 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 * @see String#toUpperCase(Locale)
 */
public class ToUpperCaseCommand extends WriterCommand{

    /**
     * 请查阅 {@link String#toUpperCase(Locale)}
     */
    private Locale locale;

    /**
     * 新建一个 {@link ToUpperCaseCommand} 实例
     * @param tempFile 要写入的临时文件
     * @param locale 请查阅 {@link String#toUpperCase(Locale)}
     * @throws FileNotFoundException
     */
    public ToUpperCaseCommand(File tempFile, Locale locale) throws FileNotFoundException {
        super(tempFile);
        this.locale = locale;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        String segStr=new String(segment).toUpperCase(locale);
        writeData(segStr);
        return false;
    }
}
