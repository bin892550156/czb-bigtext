package czb.framework.bigtext.core;

import czb.framework.bigtext.core.command.*;
import czb.framework.bigtext.core.delegate.ReadFileDelegate;
import czb.framework.bigtext.core.delegate.callback.ReadFileCallback;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * 大文本文件操作类，提供与{@link String}类似的操作文本方法
 * @author chenzhuobin
 * @since 2020/11/17 0017
 */
public class CzbBigText {


    /**
     * 每段文本最大字符长度,默认长度：2000
     */
    private final static int MAX_SEGMENT_LENGTH=20;

    /**
     * 默认编码：utf-8
     */
    private final static String CHARSET="utf-8";


    /**
     * 大文本文件
     */
    private File srcTextFile;

    /**
     * 按段读取文本文件的助手
     */
    ReadFileDelegate readFileDelegate;
    /**
     * 临时文件生成器
     */
    TempFileGenerator tempFileGenerator;

    /**
     * 该文本文件的字符数缓存，该字符数包含回车换行
     */
    private long length=-1;
    /**
     * 该文本文件的字符数缓存，该字符数不包含回车换行
     */
    private long lengthNoCRLF=-1;

    /**
     * 新建一个 {@link CzbBigText} 实例
     * @param srcTextFile 源文本文件
     */
    public CzbBigText(File srcTextFile) {
        this.srcTextFile = srcTextFile;
        readFileDelegate=new ReadFileDelegate(srcTextFile,MAX_SEGMENT_LENGTH,CHARSET);
        tempFileGenerator=new TempFileGenerator(srcTextFile);
    }

    /**
     * 新建一个 {@link CzbBigText} 实例
     * @param srcTextFile 源文本文件
     * @param tempFileGenerator 临时文件生成器
     */
    public CzbBigText(File srcTextFile, TempFileGenerator tempFileGenerator) {
        this.srcTextFile = srcTextFile;
        this.tempFileGenerator = tempFileGenerator;
    }

    /**
     * 新建一个 {@link CzbBigText} 实例
     * @param srcTextFile 源文本文件
     * @param readFileDelegate 按段读取文本文件的助手
     * @param tempFileGenerator 临时文件生成器
     */
    public CzbBigText(File srcTextFile, ReadFileDelegate readFileDelegate, TempFileGenerator tempFileGenerator) {
        this.srcTextFile = srcTextFile;
        this.readFileDelegate = readFileDelegate;
        this.tempFileGenerator = tempFileGenerator;
    }

    /**
     * 获取该文本文件的文本字符数【包含回车换行】
     * <p>该方法的返回结果会被 {@link #length} 缓存起来,后面的调用将直接返回 {@link #length}</p>
     * @return 该文本文件的文本字符数【包含回车换行】
     */
    public long length(){
        if(length!=-1){
            return length;
        }
        LengthCommand lengthCommand=new LengthCommand();
        readFile(lengthCommand);
        return length=lengthCommand.getLength();
    }

    /**
     * 获取该文本文件的文本字符数【不包含回车换行】
     * <p>该方法的返回结果会被 {@link #lengthNoCRLF} 缓存起来,后面的调用将直接返回 {@link #lengthNoCRLF}</p>
     * @return 该文本文件的文本字符数【不包含回车换行】
     */
    public long lengthNoCRLF(){
        if(lengthNoCRLF!=-1){
            return lengthNoCRLF;
        }
        LengthNoCRLFCommand lengthNoCRLFCommand = new LengthNoCRLFCommand();
        readFile(lengthNoCRLFCommand);
        return lengthNoCRLF=lengthNoCRLFCommand.getLength();
    }

    /**
     * 从第一个字符为开始查找 target 本文中第一个匹配项的索引
     * <p>对应 {@link String#indexOf(String)}</p>
     * @param toFindStr 要查找的字符串
     * @return target 本文中第一个匹配项的索引
     */
    public long indexOf(String toFindStr){
        return indexOf(toFindStr,0);
    }

    /**
     * 查找 target 本文中第一个匹配项的索引
     * <p> 对应 {@link String#indexOf(int, int)}</p>
     * @param toFindStr 要查找的字符串
     * @param fromIndex 开始搜索的索引
     * @return target 本文中第一个匹配项的索引
     */
    public long indexOf(String toFindStr,long fromIndex)  {
        IndexOfCommand command=new IndexOfCommand(toFindStr,fromIndex);
        readFile(fromIndex, toFindStr, command);
        return command.getGlobalPos();
    }

    /**
     * 本文是否包含 str
     * @param str 要查找的字符串
     * @return 如果本文包含 str ,则为 true ;否则为 false
     */
    public boolean contains(String str){
        return indexOf(str)>-1;
    }

    /**
     * 将本文的 oldStr 覆盖成 newStr
     * @param oldStr 旧字符串
     * @param newStr 新字符串
     * @return 新文本文件，该文件存放覆盖后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File replace(String oldStr, String newStr ){
        File tempFile = tempFileGenerator.getTempFile();
        try {
            readFile(0,oldStr,new ReplaceCommand(oldStr,newStr,tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 将本文第一个出现的 oldStr 覆盖成 newStr
     * @param oldStr 旧字符串
     * @param newStr 新字符串
     * @return 新文本文件，该文件存放覆盖后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File replaceFirst(String oldStr,String newStr){
        File tempFile = tempFileGenerator.getTempFile();
        try {
            readFile(0,oldStr,new ReplaceFirstCommand(oldStr,newStr,tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件
     * @param splitStr 分隔符
     * @return 新文本文件列表，这些文件存放着分割后的每段文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public List<File> split(String splitStr){
        return split(splitStr,-1);
    }

    /**
     * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件，并限制最大分割数
     * <p>当达到分割数后，即使可以分割，也不会再分割</p>
     * @param splitStr 分隔符
     * @param limit 最大分割数，当达到分割数后，即使可以分割，也不会再分割
     * @return 新文本文件列表，这些文件存放着分割后的每段文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public List<File> split(String splitStr ,int limit){
        SplitCommand command=new SplitCommand(splitStr,limit,tempFileGenerator);
        readFile(0,splitStr,command);
        return command.getTempFileList();
    }

    /**
     * 连接本文内容和 joinFiles 的文本内容，形成一个新的文本文件
     * @param delimiter 连接内容所使用的分隔符
     * @param joinFiles 要连接的文本文件
     * @return 新文本文件，该文件存放连接后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File join(String delimiter,File... joinFiles){
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new JoinFileCommand(tempFile,delimiter,joinFiles,MAX_SEGMENT_LENGTH,CHARSET));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 连接本文内容和 joinStrs 的文本内容，形成一个新的文本文件
     * @param delimiter 连接内容所使用的分隔符
     * @param joinStrs 要连接的字符串
     * @return 新文本文件，该文件存放连接后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File join(String delimiter,String... joinStrs){
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new JoinStrCommand(tempFile,delimiter,joinStrs));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 将 str 插入到文本的 offset 索引后面
     * @param offset 该文本的索引位，这里索引是基于字符的索引而不是字节索引
     * @param str 要插入的字符串
     * @return 新文本文件，该文件存放插入后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File insert(long offset, String str){
        // offset 不可以超过该文本的最大长度
        if(offset > length()){
            throw new StringIndexOutOfBoundsException("String index out of range: "+offset+", text file length: "+length());
        }
        File tempFile=new File(srcTextFile.getAbsolutePath()+".temp");
        try {
            readFile(new InsertStrCommand(tempFile,offset,readFileDelegate.getSegmentSize(), str));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 将指定文本文件的文本内容 插入该文本的 offset 索引后面
     * @param offset 该文本的索引位，这里索引是基于字符的索引而不是字节索引
     * @param file 要插入的文本文件
     * @return 新文本文件，该文件存放插入后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File insert(long offset, File file){
        // offset 不可以超过该文本的最大长度
        if(offset > length()){
            throw new StringIndexOutOfBoundsException("String index out of range: "+offset+", text file length: "+length());
        }
        File tempFile=new File(srcTextFile.getAbsolutePath()+".temp");
        try {
            readFile(new InsertFileCommand(tempFile,offset,readFileDelegate.getSegmentSize(),
                    readFileDelegate.getCharset(),file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 将该文件的文本内容的所有字母转换成小写字母
     * @return 新文本文件，该文件存放转换成小写字母后的文本内容；文件由 {@link #tempFileGenerator} 生成
     * @see String#toLowerCase()
     */
    public File toLowerCase(){
        return toLowerCase(Locale.getDefault());
    }

    /**
     * 将该文件的文本内容的所有字母转换成小写字母
     * @param locale 请查阅 {@link String#toLowerCase(Locale)}
     * @return 新文本文件，该文件存放转换成小写字母后的文本内容；文件由 {@link #tempFileGenerator} 生成
     * @see String#toLowerCase(Locale)
     */
    public File toLowerCase(Locale locale){
        if (locale == null) {
            throw new NullPointerException();
        }
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new ToLowerCaseCommand(tempFile,locale));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 将该文件的文本内容的所有字母转换成大写字母
     * @return 新文本文件，该文件存放转换成大写字母后的文本内容；文件由 {@link #tempFileGenerator} 生成
     * @see String#toUpperCase()
     */
    public File toUpperCase(){
        return toUpperCase(Locale.getDefault());
    }

    /**
     * 将该文件的文本内容的所有字母转换成大写字母
     * @param locale 请查阅 {@link String#toUpperCase(Locale)}
     * @return 新文本文件，该文件存放转换成大写字母后的文本内容；文件由 {@link #tempFileGenerator} 生成
     * @see String#toUpperCase(Locale)
     */
    public File toUpperCase( Locale locale){
        if (locale == null) {
            throw new NullPointerException();
        }
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new ToUpperCaseCommand(tempFile,locale));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 修剪该文件文本内容的开头和结尾，将开头和接口的空格去掉
     * @return 新文本文件，该文件存放修剪后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File trim(){
        long length=length();
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new TrimCommand(srcTextFile,tempFile,length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 修剪该文件文本内容的开头和结尾，将开头和接口的空格，回车换行去掉
     * @return 新文本文件，该文件存放修剪后的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File trimNoCRLF(){
        long length=length();
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new TrimNoCRLFCommand(srcTextFile,tempFile,length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 截取在该文本从 begin 到 end 范围内的文本内容
     * @param begin 开始位置
     * @param end 结束位置
     * @return 文本文件，该文件存放该文本从 begin 到 end 范围内的文本内容；文件由 {@link #tempFileGenerator} 生成
     */
    public File substring(int begin,int end){
        File tempFile=tempFileGenerator.getTempFile();
        try {
            readFile(new SubStrCommand(tempFile,begin,end,readFileDelegate.getSegmentSize()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 按段读取文本文件内容
     * <p>读取策略：在读取文本的时候，会推断读取的文本的尾部有没有 toFindStr 的开头部分， 有
     * 就需要将 toFindStr 剩余的字符读取到该段文字里</p>
     * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题,会尽可能的计算出合适的段大小</p>
     * @param toFindStr 要查询字符串
     * @param callback 按段读取文本文件时的回调专用接口
     */
    public void readFile(String toFindStr, ReadFileCallback callback){
        readFileDelegate.readFile(toFindStr,callback);
    }

    /**
     * 按段读取文本文件内容
     * <p>读取策略：在读取文本的时候，会推断读取的文本的尾部有没有 toFindStr 的开头部分， 有
     * 就需要将 toFindStr 剩余的字符读取到该段文字里</p>
     * <p>为了尽可能保证读取不会因为读取策略导致OOM的问题,会尽可能的计算出合适的段大小</p>
     * @param offset 读取文本内容的开始位置
     * @param toFindStr 要查询字符串
     * @param listener 按段读取文本文件时的回调专用接口
     */
    public void readFile(long offset, String toFindStr, ReadFileCallback listener){
        readFileDelegate.readFile(offset,toFindStr,listener);
    }

    /**
     * 按段读取文本文件内容
     * @param callback 按段读取文本文件时的回调专用接口
     */
    public void readFile(ReadFileCallback callback){
        readFileDelegate.readFile(callback);
    }

    /**
     * 按段读取文本文件内容
     * @param offset 读取文本内容的开始位置
     * @param listener 按段读取文本文件时的回调专用接口
     */
    public void readFile(long offset, ReadFileCallback listener){
        readFileDelegate.readFile(offset,listener);
    }


}
