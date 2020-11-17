package czb.framework.bigtext.core.command;

import java.io.*;

/**
 * 将本文文件的 oldStr 覆盖成 newStr 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class ReplaceCommand extends WriterCommand {

    /**
     * 旧字符串
     */
    private String oldStr;
    /**
     * 新字符串
     */
    private String newStr;

    /**
     * 新建一个 {@link ReplaceCommand} 实例
     * @param oldStr 旧字符串
     * @param newStr 新字符串
     * @param tempFile 新文本文件，该文件存放覆盖后的文本内容
     * @throws FileNotFoundException 如果 tempFile 文件不存在
     */
    public ReplaceCommand(String oldStr, String newStr, File tempFile) throws FileNotFoundException {
        super(tempFile);
        this.oldStr = oldStr;
        this.newStr = newStr;
    }


    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        String segmentStr=new String(segment);
        //如果可以覆盖
        if(canReplace(currentReadSize,segmentStr)){
            // 使用 {@link String#replace(char, char)} 进行覆盖
            segmentStr=segmentStr.replace(oldStr,newStr);
        }
        writeData(segmentStr);
        return false;
    }

    /**
     * 是否可以覆盖
     * <p>钩子方法，交由子类控制是否将{@link #oldStr} 覆盖成 {@link #newStr}</p>
     * <p>默认实现直接返回true，表示所有的 {@link #oldStr} 都覆盖成 {@link #newStr}</p>
     * @param currentReadSize 当前已读字符数
     * @param segmentStr 这段文本的内容
     * @return 返回 true 表示可以覆盖;否则为 false
     */
    protected boolean canReplace(long currentReadSize, String segmentStr) {
        return true;
    }
}
