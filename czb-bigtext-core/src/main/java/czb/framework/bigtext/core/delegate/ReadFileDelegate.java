package czb.framework.bigtext.core.delegate;

import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;
import czb.framework.bigtext.core.exception.BigTextException;

import java.io.*;

/**
 *
 */
/**
 * 按段读取文本文件的助手类型
 * <p><b>读取策略：</b>在读取文本的时候，会推断读取的文本的尾部有没有 toFindStr 的开头部分，
 * 有就需要将 toFindStr 剩余的字符读取到该段文字里。详情请看：{@link #readFile(long, String, ReadFileCallback)}</p>
 * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题，会调用 {@link #calSuitableSegmentSize(int)} 得到合适
 * 的段文本大小。</p>
 *
 * @author chenzhuobin
 * @since 2020/11/13 0013
 */
public class ReadFileDelegate {

    /**
     * 每段最多读取的字符数
     * <p>【框架尽可能保证不会超过 segmentSize,可能出现超过 segmentSize 请看 {@link #readFile(long, String, ReadFileCallback)}】</p>
     */
    private Integer segmentSize;
    /**
     * 文本编码
     */
    private String charset;
    /**
     * 文本文件
     */
    private File textFile;

    /**
     * 新建一个 {@link ReadFileDelegate} 实例
     * @param textFile 文本文件
     * @param segmentSize 每段最多读取的字符数
     *                    【框架尽可能保证不会超过 segmentSize,可能出现超过 segmentSize 请看 {@link #readFile(long, String, ReadFileCallback)}】
     * @param charset 文本编码
     */
    public ReadFileDelegate(File textFile, int segmentSize, String charset){
        this.textFile = textFile;
        this.segmentSize =segmentSize;
        this.charset =charset;
    }


    /**
     * 按段读取文本文件内容
     * <p><b>读取策略：</b>在读取文本的时候，会推断读取的文本的尾部有没有 toFindStr 的开头部分，
     * 有就需要将 toFindStr 剩余的字符读取到该段文字里。</p>
     * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题，会调用 {@link #calSuitableSegmentSize(int)} 得到合适
     * 的段文本大小。</p>
     * @param toFindStr 要查询字符串
     * @param callback 按段读取文本文件时 的回调专用接口
     */
    public void readFile(String toFindStr, ReadFileCallback callback){
        readFile(0,toFindStr,callback);
    }

    /**
     * 按段读取文本文件内容
     * <p><b>读取策略：</b>在读取文本的时候，会推断读取的文本的尾部有没有 toFindStr 的开头部分，
     * 有就需要将 toFindStr 剩余的字符读取到该段文字里。</p>
     * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题，会调用 {@link #calSuitableSegmentSize(int)} 得到合适
     * 的段文本大小。</p>
     * @param offset 偏移字符数
     * @param toFindStr 要查询字符串
     * @param callback 按段读取文本文件时 的回调专用接口
     */
    public void readFile(long offset, String toFindStr, ReadFileCallback callback){
        try(InputStreamReader reader=new InputStreamReader(new FileInputStream(textFile), charset)){
            //跳过 offset 个字符数
            reader.skip(offset);
            // 计算合适的段大小
            int pad= calSuitableSegmentSize(toFindStr.length());
            char[] cbuff=new char[pad];
            int read= reader.read(cbuff,0,pad) ;
            long currnetReadSize=read;
            char[] newCbuff=cbuff;
            while (read!=-1){
                //获取 source 的尾部匹配到 target 的开头部分时，target 剩余未匹配的字符数
                int shouldReadCount = getMatchRemainCount(cbuff, toFindStr.toCharArray());
                if(shouldReadCount>0){
                    newCbuff=new char[read+shouldReadCount];
                    System.arraycopy(cbuff,0,newCbuff,0,cbuff.length);
                    currnetReadSize+=reader.read(newCbuff,pad,shouldReadCount);
                }else{
                    newCbuff=new char[read];
                    System.arraycopy(cbuff,0,newCbuff,0,read);
                }
                //当前读取完一段文本后回调
                if(callback.onSegmentRead(currnetReadSize,newCbuff)){
                    break;
                }
                read = reader.read(cbuff,0,pad);
                if(read!=-1){
                    currnetReadSize+=read;
                }
            }
            //当读完文本文件内容后回调
            callback.onCompleteRead(currnetReadSize,newCbuff);
        } catch (UnsupportedEncodingException e) {
            throw new BigTextException(" no support charset: "+charset,e);
        } catch (FileNotFoundException e) {
            throw new BigTextException(" no found text file: "+textFile.getAbsolutePath(),e);
        } catch (IOException e) {
            throw new BigTextException(" read text file: "+textFile.getAbsolutePath()+",throw IO exception: ",e);
        }
    }

    /**
     * 按段读取文本文件内容
     * @param callback 按段读取文本文件时 的回调专用接口
     */
    public void readFile(ReadFileCallback callback){
        readFile(0,callback);
    }

    /**
     * 按段读取文本文件内容
     * @param offset 偏移字符数
     * @param listener 按段读取文本文件时 的回调专用接口
     */
    public void readFile(long offset, ReadFileCallback listener){
        try(InputStreamReader reader=new InputStreamReader(new FileInputStream(textFile), charset)){
            reader.skip(offset);
            int buffSize= segmentSize;
            char[] cbuff=new char[buffSize];
            int read= reader.read(cbuff,0,buffSize) ;
            long currnetReadSize=read;
            char[] newCbuff=cbuff;
            while (read!=-1){
                newCbuff=new char[read];
                System.arraycopy(cbuff,0,newCbuff,0,read);
                //当前读取完一段文本后回调
                if(listener.onSegmentRead(currnetReadSize,newCbuff)){
                    break;
                }
                read= reader.read(cbuff,0,buffSize);
                if(read!=-1){
                    currnetReadSize+=read;
                }
            }
            //当读完文本文件内容后回调
            listener.onCompleteRead(currnetReadSize,newCbuff);
        } catch (UnsupportedEncodingException e) {
            throw new BigTextException(" no support charset: "+charset,e);
        } catch (FileNotFoundException e) {
            throw new BigTextException(" no found text file: "+textFile.getAbsolutePath(),e);
        } catch (IOException e) {
            throw new BigTextException(" read text file: "+textFile.getAbsolutePath()+",throw IO exception: ",e);
        }

    }

    /**
     * 计算合适的段大小
     * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题，合适的段长度应该为 segmentSize - toFindStrLength 的
     * 差 ，这样会导致读取速度很慢，但是这样比较安全。</p>
     * <p>虽然 segmentSize - toFindStrLength 的差比较安全，但是差值出现下列情况时，还是会导致段文本在找到要查找的文本时，
     * 大于 segmentSize 的情况：</p>
     * <ol>
     *  <li>如果 segmentSize == toFindStrLength  || 如果 segmentSize < toFindStrLength 时，
     *  则为 segmentSize/5 。因为至少要读点数据才能保证文本的读取正常 </li>
     *  <li>如果 toFindStrLength > 2*segmentSize,则为 toFindStrLength,因为太大了，没有足够的大小承载要查找的文本，但是为了保证
     *  功能的可用性，只能用 toFindStrLength.</li>
     * </ol>
     * @param toFindStrLength 要查找的文件大小
     * @return 合适的段大小
     */
    protected int calSuitableSegmentSize(int toFindStrLength){
        int pad= segmentSize - toFindStrLength;
        if(Math.abs(pad) > segmentSize){
            pad=toFindStrLength;
        }
        if(pad<=0){
            pad=segmentSize/5;
        }
        return pad;
    }

    /**
     * 获取 source 的尾部匹配到 target 的开头部分时，target 剩余未匹配的字符数
     * @param source 源字符串字符数组
     * @param target 目标字符串字符数组
     * @return 返回 target 减去 与source尾部匹配的字符数 所得到的剩余字符数 ；返回 -1 表示 source 与 target 完全没有匹配
     */
    public int getMatchRemainCount(char[] source, char[] target){
        char first = target[0];
        int sourceLength=source.length;
        int targetLength=target.length;
        for (int i = 0 ; i < sourceLength; i++) {
            //匹配成功的字符数
            int matchCount=0;
            //查找第一个匹配的字符
            if (source[i] != first) {
                while (++i < sourceLength && source[i] != first);
            }
            matchCount++;
            //查找剩余的字符
            if (i < sourceLength) {
                int j = i + 1;
                int k ;
                for ( k = 1; j < sourceLength && k<targetLength && source[j]
                        == target[k]; j++, k++){
                    matchCount++;
                }
                if (j == sourceLength) {
                    return targetLength-matchCount;
                }
            }
        }
        return -1;
    }

    /**
     * 获取 每段最多读取的字符数
     * <p>【框架尽可能保证不会超过 segmentSize,可能出现超过 segmentSize 请
     * 看 {@link #readFile(long, String, ReadFileCallback)}】</p>
     * @see #segmentSize
     */
    public Integer getSegmentSize() {
        return segmentSize;
    }

    /**
     * 获取 文本编码
     * @see #charset
     */
    public String getCharset() {
        return charset;
    }
}
