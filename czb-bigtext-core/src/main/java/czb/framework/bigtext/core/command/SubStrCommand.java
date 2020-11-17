package czb.framework.bigtext.core.command;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 截取在该文本从 begin 到 end 范围内的文本内容的命令
 * @author chenzhuobin
 * @since 2020/11/17 0017
 */
public class SubStrCommand extends WriterCommand{

    /**
     * 开始位置
     */
    private long begin;
    /**
     * 结束位置
     */
    private long end;
    /**
     * 当前段数
     */
    private int segTimes;
    /**
     * 开始位置的所在的段数
     */
    private int stSegTimes;
    /**
     * 结束位置的所在的段数
     */
    private long endSegTimes;
    /**
     * 是否在 {@link #begin} 到 {@link #end} 的范围内
     * <p>当在此范围内的数据，需要写入新文件中 </p>
     */
    private boolean onScope;
    /**
     * 最大段大小
     */
    private int segmentSize;

    /**
     * 新建一个 {@link SubStrCommand} 的实例
     * @param tempFile 要写入的临时文件
     * @param begin 开始位置
     * @param end 结束位置
     * @param segmentSize 最大段大小
     * @throws FileNotFoundException 当 tempFile 不存在时抛出
     */
    public SubStrCommand(File tempFile, long begin, long end, int segmentSize) throws FileNotFoundException {
        super(tempFile);
        this.begin = begin;
        this.end = end;
        this.segmentSize=segmentSize;
        stSegTimes=Long.valueOf(begin /segmentSize).intValue();
        endSegTimes= Long.valueOf(end / segmentSize).intValue();
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        int segLen = segment.length;
        // 当当前段数达到开始位置所在的段数
        if(stSegTimes==segTimes ){
            //计算出剩余偏移位
            long remain= begin % segmentSize ;
            int intRemain = Long.valueOf(remain).intValue();
            //保证剩余偏移位不会超过 segLen
            intRemain= Math.min(intRemain, segLen);
            String segmentStr=new String(segment);
            writeData(segmentStr.substring(0, intRemain));
            onScope=true;
        }
        // 当当前段数达到结束位置所在的段数
        if(endSegTimes==segTimes){
            //计算出剩余偏移位
            long remain= end % segmentSize ;
            int intRemain = Long.valueOf(remain).intValue();
            //保证剩余偏移位不会超过 segLen
            intRemain= Math.min(intRemain, segLen);
            String segmentStr=new String(segment);
            writeData(segmentStr.substring(0, intRemain).toCharArray());
            return true;
        }
        // 当在 {@link #begin} 到 {@link #end} 的范围内
        if(onScope){
            writeData(segment);
        }
        segTimes++;
        return false;
    }
}
