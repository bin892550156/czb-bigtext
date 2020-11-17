package czb.framework.bigtext.core.command;

import java.io.*;

/**
 * 将 str 插入到文本的 offset 索引后面 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class InsertStrCommand extends InsertCommand {

    /**
     * 要插入的字符串
     */
    private String str;

    public InsertStrCommand(File tempFile,long offset, int segmentSize,String str) throws FileNotFoundException {
        super(tempFile,offset,segmentSize);
        this.str = str;
    }


    @Override
    public void insert(long currentReadSize, char[] segment)  {
        writeData(str);
    }
}
