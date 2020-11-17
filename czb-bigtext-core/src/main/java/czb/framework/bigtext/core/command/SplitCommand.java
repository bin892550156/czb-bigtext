package czb.framework.bigtext.core.command;

import czb.framework.bigtext.core.TempFileGenerator;
import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;
import czb.framework.bigtext.core.exception.BigTextException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件 的指令
 * <p>如果需要，可通过设置 {@link #limit} 限制最大分割数，当达到分割数后，即使可以分割，也不会再分割</p>
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class SplitCommand implements ReadFileCallback {

    /**
     * 新文本文件列表，这些文件存放着分割后的每段文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    private List<File> tempFileList=new ArrayList<>();
    /**
     * 当前切分次数
     */
    private int nowSplitTimes=1;
    /**
     * 新文本文件序号
     */
    private int fileNum=1;
    /**
     * 当前操作的新文本文件
     */
    private File tempFile;
    /**
     * 当前操作的新文本文件的字符输出流
     */
    private OutputStreamWriter writer;

    /**
     * 分隔符
     */
    private String splitStr;
    /**
     *  限制最大分割数，当达到分割数后，即使可以分割，
     *  也不会再分割；如果为-1表示不限制
     */
    private int limit;
    /**
     * 临时文件生成器
     */
    private TempFileGenerator tempFileGenerator;

    /**
     * 新建一个 {@link SplitCommand} 实例
     * @param splitStr 分隔符
     * @param limit 限制最大分割数，当达到分割数后，即使可以分割，也不会再分割；
     *              如果为-1表示不限制
     * @param tempFileGenerator 临时文件生成器
     */
    public SplitCommand(String splitStr, int limit, TempFileGenerator tempFileGenerator) {
        this.splitStr = splitStr;
        this.limit = limit;
        this.tempFileGenerator=tempFileGenerator;
    }

    @Override
    public boolean onSegmentRead(long currentReadSize, char[] segment) {
        String segmentStr=new String(segment);
        try{
            // 初始化输出流
            if(writer==null){
                buildNewWriter();
            }
            // 如果当前分割次数还没有超过最大分割数 且 这段文本包含分割符
            if((limit == -1 || nowSplitTimes<limit) && segmentStr.contains(splitStr)){
                // 使用 {@link String#split(String)}切分字符串
                String[] splitSegmentStrArr = segmentStr.split(splitStr);
                if(splitSegmentStrArr.length>0){
                    for (int i = 0; i < splitSegmentStrArr.length; i++) {
                        String splitElementStr=splitSegmentStrArr[i];
                        writer.write(splitElementStr);
                        // i!=splitSegmentStrArr.length-1：最后一个字符串有可能是因为分段读取原因，所以不完整，
                        //      所以不需要构建新的文本文件
                        // segmentStr.endsWith(splitStr)：最后一个字符串有可能是分隔符，这种这种情况是需要构建新的文本文件的。
                        if(i!=splitSegmentStrArr.length-1 || segmentStr.endsWith(splitStr)){
                            // 关闭当前的文本文件流，
                            writer.close();
                            // 将当前文件添加到 新文本文件列表
                            tempFileList.add(tempFile);
                            //构建新文本文件
                            buildNewWriter();
                            //当前切分次数累加1
                            nowSplitTimes++;
                        }
                    }
                }
            }else{
                //如果这段文字不包含分隔符，或者当前分割数已经达到最大限制数
                writer.write(segmentStr);
            }
        }catch (FileNotFoundException e) {
           throw new BigTextException("no found temp file:"+tempFile.getAbsolutePath(),e);
        } catch (IOException e) {
           throw new BigTextException(" write in tempFile: "+tempFile.getAbsolutePath()+" fail",e);
        }
        return false;
    }

    @Override
    public void onCompleteRead(long currentReadSize, char[] lastSegment) {
        //最后一个新文本文件的收尾工作
        try {
            writer.close();
            tempFileList.add(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建 当前操作的新文本文件 {@link #tempFile} ,当前操作的新文本文件的字符输出流 {@link #writer},新文本文件序号+1
     * @throws FileNotFoundException 如果 {@link #tempFile} 不存在
     */
    private void buildNewWriter() throws FileNotFoundException {
        tempFile =tempFileGenerator.getTempFile(fileNum);
        writer=new OutputStreamWriter(new FileOutputStream(tempFile));
        fileNum++;
    }

    /**
     * 新文本文件列表，这些文件存放着分割后的每段文本内容；文件由 {@link #tempFileGenerator} 生成
     * @see #tempFileList
     */
    public List<File> getTempFileList() {
        return tempFileList;
    }
}
