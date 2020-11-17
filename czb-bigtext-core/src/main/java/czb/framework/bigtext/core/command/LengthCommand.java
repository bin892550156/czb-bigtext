package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;

/**
 * 获取该文本文件的文本字符数【包含回车换行】 的指令
 * @author chenzhuobin
 * @since 2020/11/13 0013
 */
public class LengthCommand implements ReadFileCallback {

    /**
     * 该文本文件的文本字符数【包含回车换行】
     */
    private long length;

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        return false;
    }

    @Override
    public void onCompleteRead(long currentReadSize, char[] lastSegment) {
        length=currentReadSize;
    }

    /**
     * 该文本文件的文本字符数【包含回车换行】
     * @see #length
     */
    public long getLength() {
        return length;
    }
}
