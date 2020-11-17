package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.ReadFileDelegate;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 将指定文本文件的文本内容 插入该文本的 offset 索引后面 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class InsertFileCommand extends InsertCommand{

    /**
     * 要插入的文本文件
     */
    private File insertFile;
    /**
     * 段的最大大小
     * <p>用于 计算出该 offset 所在的段，也可以理解成 找到 offset 所需要的段数 </p>
     * <p>也用于 按段读取 {@link #insertFile} 时的最大段大小</p>
     */
    private int segmentSize;
    /**
     * 读取 {@link #insertFile} 所用的编码
     */
    private String charset;

    /**
     * 新建一个 {@link InsertFileCommand} 实例
     * @param tempFile 要插入的文本文件
     * @param offset 该文本的索引位，这里索引是基于字符的索引而不是字节索引
     * @param segmentSize 段的最大大小。用于 计算出该 offset 所在的段，也可以理解成 找到 offset 所需要的段数 ；
     *                      也用于 按段读取 {@link #insertFile} 时的最大段大小
     * @param charset 读取 insertFile 所用的编码
     * @param insertFile 要插入的文本文件
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public InsertFileCommand(File tempFile, long offset, int segmentSize,String charset,File insertFile) throws FileNotFoundException {
        super(tempFile, offset, segmentSize);
        this.insertFile = insertFile;
        this.segmentSize=segmentSize;
        this.charset=charset;
    }

    @Override
    public void insert(long currentReadSize, char[] segment) {
        ReadFileDelegate delegate=new ReadFileDelegate(insertFile,segmentSize,charset);
        delegate.readFile((oCurrentReadSize, oSegment) -> {
            writeData(oSegment);
            return false;
        });
    }
}
