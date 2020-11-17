package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;
import czb.framework.bigtext.core.exception.BigTextException;

import java.io.*;


/**
 * 将数据写入指定文件的命令，该类是抽象类，交由子类实现自己业务的数据写入。
 * <p>该类提供写入指定文件的统一方法和关闭指定文件的输出流的方法，</p>
 * <p>当读完文本文件内容后时，该类会自动关闭输出流，所以子类执行关注写入指定文件的数据</p>
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public abstract class WriterCommand implements ReadFileCallback {

    /**
     * 要写入的临时文件
     */
    private File tempFile;
    /**
     * 写入 {@link #tempFile} 的文本输出流
     */
    private OutputStreamWriter writer;

    /**
     * 新建一个 {@link WriterCommand} 实例
     * @param tempFile 要写入的临时文件
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public WriterCommand(File tempFile) throws FileNotFoundException {
        this.tempFile = tempFile;
        writer=new OutputStreamWriter(new FileOutputStream(tempFile));
    }

    /**
     * 当读完文本文件内容后回调
     * <p>回调时，关闭 {@link #writer}</p>
     * @param currentReadSize 当前已读字符数
     * @param lastSegment 最后一段文本内容
     */
    @Override
    public void onCompleteRead(long currentReadSize, char[] lastSegment) {
       closeWriter();
    }

    /**
     * 将 data 写入 {@link #tempFile}
     * @param data 要写入 {@link #tempFile} 的文本数据
     */
    public void writeData(String data){
        try {
            writer.write(data);
        } catch (IOException e) {
            throw new BigTextException(" write in tempFile "+tempFile.getAbsolutePath()+" fail",e);
        }
    }

    /**
     * 将 data 写入 {@link #tempFile}
     * @param data  要写入 {@link #tempFile} 的文本数据
     */
    public void writeData(char[] data){
        try {
            writer.write(data);
        } catch (IOException e) {
            throw new BigTextException(" write in tempFile: "+tempFile.getAbsolutePath()+" fail",e);
        }
    }

    /**
     * 关闭 {@link #writer}
     */
    public void closeWriter(){
        try {
            writer.close();
        } catch (IOException e) {
            throw new BigTextException(" close tempFile: "+tempFile.getAbsolutePath()+" fail",e);
        }
    }

    /**
     * 获取要写入的临时文件
     * @see #tempFile
     */
    public File getTempFile() {
        return tempFile;
    }

    /**
     * 获取写入 {@link #tempFile} 的文本输出流
     * @see #writer
     */
    public OutputStreamWriter getWriter() {
        return writer;
    }
}
