package czb.framework.bigtext.core.command;


import java.io.*;

/**
 * 修剪该文件文本内容的开头和结尾，将开头和接口的空格去掉 的命令
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class TrimCommand extends WriterCommand {

    /**
     * 源文件
     */
    private File srcFile;
    /**
     * 修剪后的结束索引位
     */
    private long endLimit;
    /**
     * 是否处于开头
     */
    private boolean first=true;

    /**
     * 新建一个 {@link TrimCommand} 实例
     * @param srcfile 源文件
     * @param tempFile 要写入的临时文件
     * @param srcFileLength 源文件的文本内容总字符大小
     * @throws IOException 当 tempFile 不存在时抛出
     */
    public TrimCommand(File srcfile,File tempFile,long srcFileLength) throws IOException {
        super(tempFile);
        this.srcFile = srcfile;
        //确定修剪结尾索引位
        long end=determineTrimEndPos();
        //计算出修剪后的结束索引位
        this.endLimit = srcFileLength-end;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        // 开头的修剪
        if(first){
            int st=0;
            int len=segment.length;
            while ((st < len) && (isTrminChar(segment[st]))) {
                st++;
            }
            int diff=len-st;
            //因为时按段读取的，所以可能整段都是要修剪的字符，diff==0才意味着已经到了不是修剪字符的那段文字里。
            first=diff==0;
            if(!first){
                char[] newSeg=new char[diff];
                System.arraycopy(segment,st,newSeg,0,diff);
                writeData(newSeg);
            }
        }else if (currentReadSize>=endLimit){//结尾的修剪
            long diff = currentReadSize - endLimit;
            int len=segment.length-Long.valueOf(diff).intValue();
            char[] newSeg=new char[len];
            System.arraycopy(segment,0,newSeg,0,len);
            writeData(newSeg);
            return true;
        }else{//中间部分
            writeData(segment);
        }
        return false;
    }


    /**
     * 确定修剪结尾索引位
     * <p>倒叙读取文件内容</p>
     * @return 修剪结尾索引位
     * @throws IOException 读取源文件时出现的IO异常
     */
    private long determineTrimEndPos() throws IOException {
        RandomAccessFile rf =new RandomAccessFile(srcFile,"r");
        long len=rf.length();
        long start = rf.getFilePointer();
        long nextend = start + len - 1;
        rf.seek(nextend);
        long endOffset=0;
        while (nextend > start) {
            int c = rf.read();
            if(isTrminChar((char) c)){
                endOffset++;
            }else{
                break;
            }
            nextend--;
            rf.seek(nextend);
        }
        rf.close();
        return endOffset;
    }

    /**
     * 是否属于修剪后的字符
     * <p>默认实现：如果 c 是 空格就返回 true;</p>
     * @param c 要检查的字符
     * @return 如果属于，返回 true;否则，返回false
     */
    protected boolean isTrminChar(char c){
        return c==' ';
    }
}
