package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;

/**
 * 查找指定字符串,返回本文中第一个匹配项的索引 的指令
 * <p>对应 {@link String#indexOf(String, int)} 方法</p>
 *
 * @author chenzhuobin
 * @since  2020/11/13 0013
 */
public class IndexOfCommand implements ReadFileCallback {

    /**
     * {@link #toFindStr} 在该文本中的第一个字符索引位置
     */
    private long globalPos;
    /**
     * 查找指定字符串
     */
    private String toFindStr;

    /**
     * 新建一个 {@link IndexOfCommand} 实例
     * @param toFindStr 当前索引位置
     * @param fromIndex 查找指定字符串
     */
    public IndexOfCommand(String toFindStr, long fromIndex){
        this.toFindStr=toFindStr;
        this.globalPos=fromIndex;
    }

    /**
     * 将 segment 包装成 字符串 调用 {@link String#indexOf(String)} 得到 {@link #toFindStr} 在
     * segment 的 索引位，然后通过 currentReadSize 和 索引位 计算出 {@link #globalPos}
     * @param currentReadSize 当前已读字符数
     * @param segment 这段文本的内容
     * @return 当找到索引位会返回true,以终止文本的读取
     */
    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        String segmentStr=new String(segment);
        int segmentOffset = segmentStr.indexOf(toFindStr);
        if(segmentOffset!=-1){
            globalPos += currentReadSize -(segment.length-segmentOffset);
            return true;
        }else{
            return false;
        }
    }

    /**
     * {@link #toFindStr} 在该文本中的第一个字符索引位置
     */
    public long getGlobalPos() {
        return globalPos;
    }
}
