package czb.framework.bigtext.core.command;


import java.io.*;

/**
 * 将指定内容插入该文本的 {@link #offset} 索引后面 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public abstract class InsertCommand extends WriterCommand {

    /**
     * 该文本的索引位，这里索引是基于字符的索引而不是字节索引
     */
    private long offset;

    /**
     * 当前段数
     */
    private int segTimes=0;
    /**
     * 目前段数
     */
    private int targetSegTimes;
    /**
     * 段的最大大小
     */
    private int segmentSize;

    /**
     * 新建一个 {@link InsertCommand} 实例
     * @param tempFile 要写入的临时文件
     * @param offset 该文本的索引位，这里索引是基于字符的索引而不是字节索引
     * @param segmentSize 段的最大大小
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public InsertCommand(File tempFile, long offset, int segmentSize) throws FileNotFoundException {
        super(tempFile);
        this.offset = offset;
        this.segmentSize=segmentSize;
        //计算出该 offset 所在的段，也可以理解成 找到 offset 所需要的段数
        targetSegTimes=Long.valueOf(offset/segmentSize).intValue();
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        int segLen = segment.length;
        // 当当前段数 达到 目标段数，意味着该段文字中有符合 offset 的索引位
        if(targetSegTimes==segTimes){
            //计算出剩余多少个索引位
            long remain=offset % segmentSize ;
            int intDiff = Long.valueOf(remain).intValue();
            //如果是最后一段，segLen 很可能不等于 segmentSize ，导致出现数组越界问题，为了保证功能的容错性，
            //  所以直接取 segLen 作为该段的索引偏移
            intDiff= Math.min(intDiff, segLen);
            String segmentStr=new String(segment);
            //截取该段指定索引之前的文本内容，并输出到 tempFile
            writeData(segmentStr.substring(0, intDiff));
            //插入指定内容，并输出到 tempFile
            insert(currentReadSize,segment);
            //截取该段指定索引之后的文本内容，并输出到 tempFile
            writeData(segmentStr.substring(intDiff));
        }else{
            writeData(segment);
        }
        segTimes++;
        return false;
    }

    /**
     * 输出要插入的内容，由子类实现具体输出的内容
     * @param currentReadSize 当前已读字符数
     * @param segment  这段文本的内容
     */
    public abstract void insert(long currentReadSize, char[] segment);
}
