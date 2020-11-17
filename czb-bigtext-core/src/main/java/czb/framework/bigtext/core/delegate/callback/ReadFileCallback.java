package czb.framework.bigtext.core.delegate.callback;


/**
 * {@link czb.framework.bigtext.core.delegate.ReadFileDelegate} 按段读取文本文件时
 * 的回调专用接口
 *
 * @author chenzhuobin
 * @since 2020/11/13 0013
 */
public interface ReadFileCallback {
    /**
     * 当前读取完一段文本后回调
     * @param currentReadSize 当前已读字符数
     * @param segment 这段文本的内容
     * @return 如果返回true,则不再读取下一段文本，直接终止读取；
     *      否则继续读取下一段文本指定文本完全读完。
     */
    boolean onSegmentRead(long currentReadSize,char[] segment);

    /**
     * 当读完文本文件内容后回调
     * @param currentReadSize 当前已读字符数
     * @param lastSegment 最后一段文本内容
     */
    default void onCompleteRead(long currentReadSize,char[] lastSegment) {}
}