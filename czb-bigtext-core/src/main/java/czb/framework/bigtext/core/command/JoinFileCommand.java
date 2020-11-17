package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.ReadFileDelegate;
import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;

import java.io.*;

/**
 * 连接本文内容和 files 的文本内容，形成一个新的文本文件 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class JoinFileCommand extends WriterCommand {

    /**
     * 连接内容所使用的分隔符
     */
    private String delimiter;
    /**
     * 要连接本文的文本文件
     */
    private File[] joinFiles;
    /**
     * 读取 {@link #joinFiles} 每段文本内容的最大大小
     */
    private int maxSegmentLength;
    /**
     * 读取 {@link #joinFiles} 文本内容所用到的编码
     */
    private String charset;


    /**
     * 新建一个 {@link JoinFileCommand} 实例
     * @param tempFile 要写入的临时文件
     * @param delimiter 连接内容所使用的分隔符
     * @param joinFiles 要连接本文的文本文件
     * @param maxSegmentLength 读取 {@link #joinFiles} 每段文本内容的最大大小
     * @param charset 读取 {@link #joinFiles} 文本内容所用到的编码
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public JoinFileCommand(File tempFile, String delimiter, File[] joinFiles, int maxSegmentLength, String charset) throws FileNotFoundException {
        super(tempFile);
        this.delimiter = delimiter;
        this.joinFiles = joinFiles;
        this.maxSegmentLength = maxSegmentLength;
        this.charset = charset;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        writeData(segment);
        return false;
    }

    @Override
    public void onCompleteRead(long currentReadSize, char[] lastSegment) {
        writeData(delimiter);
        //使用 {@link ReadFileDelegate} 按段读取要连接的文件的文本内容，然后直接输出到 tempFile 里
        for (int i = 0; i < joinFiles.length; i++) {
            File file= joinFiles[i];
            ReadFileDelegate delegate=new ReadFileDelegate(file,maxSegmentLength,charset);
            int finalI = i;
            delegate.readFile(new ReadFileCallback() {
                @Override
                public boolean onSegmentRead(long currentReadSize, char[] segment) {
                    writeData(segment);
                    return false;
                }

                @Override
                public void onCompleteRead(long currentReadSize, char[] lastSegment) {
                    if(finalI!= joinFiles.length-1){
                        writeData(delimiter);
                    }
                }
            });
        }
        //因为父级方法默认会关闭 tempFile的输出流，所以要放到方法的最后再执行，以免出现IO关闭问题
        super.onCompleteRead(currentReadSize,lastSegment);
    }


}
