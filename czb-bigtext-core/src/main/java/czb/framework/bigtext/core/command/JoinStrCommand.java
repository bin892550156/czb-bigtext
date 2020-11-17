package czb.framework.bigtext.core.command;

import java.io.*;

/**
 * 连接本文内容和 joinStrs 的文本内容，形成一个新的文本文件 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class JoinStrCommand extends WriterCommand {
    /**
     * 连接内容所使用的分隔符
     */
    private String delimiter;
    /**
     * 连接本文的字符串
     */
    private String[] joinStrs;

    /**
     * 新建一个 {@link JoinStrCommand}
     * @param tempFile 要写入的临时文件
     * @param delimiter 连接内容所使用的分隔符
     * @param joinStrs 要连接本文的文本文件
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public JoinStrCommand(File tempFile, String delimiter, String[] joinStrs) throws FileNotFoundException {
        super(tempFile);
        this.delimiter = delimiter;
        this.joinStrs = joinStrs;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        writeData(segment);
        return false;
    }

    @Override
    public void onCompleteRead(long currentReadSize, char[] lastSegment) {
        for (String str : joinStrs) {
            writeData(delimiter);
            writeData(str);
        }
        //因为父级方法默认会关闭 tempFile的输出流，所以要放到方法的最后再执行，以免出现IO关闭问题
        super.onCompleteRead(currentReadSize,lastSegment);
    }


}
