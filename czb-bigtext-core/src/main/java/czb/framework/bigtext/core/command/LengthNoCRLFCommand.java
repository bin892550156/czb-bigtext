package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;

/**
 * 获取该文本文件的文本字符数【不包含回车换行】 的指令
 * @author chenzhuobin
 * @since 2020/11/13 0013
 */
public class LengthNoCRLFCommand implements ReadFileCallback {

    /**
     * 该文本文件的文本字符数【不包含回车换行】
     */
    private long length =0;

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        for (char c:segment) {
            if(c=='\n'||c=='\r'){
                continue;
            }
            length++;
        }
        return false;
    }

    /**
     * 该文本文件的文本字符数【包含回车换行】
     * @see #length
     */
    public long getLength() {
        return length;
    }
}
