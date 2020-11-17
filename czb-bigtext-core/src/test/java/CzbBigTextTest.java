import czb.framework.bigtext.core.CzbBigText;
import czb.framework.bigtext.core.delegate.ReadFileDelegate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * {@link CzbBigText} 的测试用例
 */
public class CzbBigTextTest {

    /**
     * 大文本文件操作类，提供与{@link String}类似的操作文本方法
     */
    CzbBigText czbBigText;
    /**
     * 源文本文件
     */
    File srcTextfile;

    private static final Logger log = LoggerFactory.getLogger(CzbBigTextTest.class);

    @Before
    public void init(){
        srcTextfile = new File("../psd/zuowen.txt");
        czbBigText=new CzbBigText(srcTextfile);
    }

    /**
     * 从第一个字符为开始查找 target 本文中第一个匹配项的索引
     * <p>对应 {@link String#indexOf(String)}</p>
     */
    @Test
    public void test_indexOf(){
        String target="防身的武器";
        String content = readFileContent();
        Assert.assertEquals(czbBigText.indexOf(target),content.indexOf(target));
        Assert.assertEquals(czbBigText.indexOf(target,10),content.indexOf(target,10));
    }

    /**
     * 获取该文本文件的文本字符数【包含回车换行】
     */
    @Test
    public void test_length(){
        Assert.assertEquals(czbBigText.length(),readFileContent().length());
    }

    /**
     * 获取该文本文件的文本字符数【不包含回车换行】
     */
    @Test
    public void test_lengthNoCRLF(){
        Assert.assertEquals(czbBigText.lengthNoCRLF(),reaFileContentNoCRLF().length());
    }

    /**
     * 获取 source 的尾部匹配到 target 的开头部分时，target 剩余未匹配的字符数
     */
    @Test
    public void test_detemineTailAppendCharCount(){
        String source="hello world";
        String target="ldff";
        ReadFileDelegate delegate=new ReadFileDelegate(srcTextfile,20,"utf-8");
        Assert.assertEquals(delegate.getMatchRemainCount(source.toCharArray(),target.toCharArray()),2);
    }


    /**
     * 将本文的 oldStr 覆盖成 newStr
     */
    @Test
    public void test_replace(){
        String oldStr="如乌龟形的、山形的、触须形的……还有五颜六色的花朵，如鲜红的，嫩黄的，湛蓝的……我们家族的";
        String newStr="what，你在说什么？";
        Assert.assertEquals(readFileContent(czbBigText.replace(oldStr,newStr)), readFileContent().replace(oldStr,newStr ));
    }

    /**
     * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件
     */
    @Test
    public void test_split(){
        String[] contentArr = readFileContent().split("我");
        List<File> files = czbBigText.split("我");
        for (int i = 0; i < files.size(); i++) {
            File tempFile = files.get(i);
            String content = contentArr[i];
            log.debug("file.name={},content={}",tempFile.getName(),content);
            Assert.assertEquals(readFileContent(tempFile),content);
        }
    }

    /**
     * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件
     */
    @Test
    public void test_split2(){
        String[] contentArr = readFileContent().split("左右哦");
        List<File> files = czbBigText.split("左右哦");
        for (int i = 0; i < files.size(); i++) {
            File tempFile = files.get(i);
            String content = contentArr[i];
            log.debug("file.name={},content={}",tempFile.getName(),content);
            Assert.assertEquals(readFileContent(tempFile),content);
        }
    }

    /**
     * 使用 splitStr 作为分隔符 对本文进行切分成多个文本文件，并限制最大分割数
     * 当达到分割数后，即使可以分割，也不会再分割
     */
    @Test
    public void test_splitLimit(){
        String[] contentArr = readFileContent().split("我",3);
        List<File> files = czbBigText.split("我",3);
        for (int i = 0; i < files.size(); i++) {
            File tempFile = files.get(i);
            String content = contentArr[i];
            log.debug("file.name={},content={}",tempFile.getName(),content);
            Assert.assertEquals(readFileContent(tempFile),content);
        }
    }

    /**
     * 连接本文内容和 joinFiles 的文本内容，形成一个新的文本文件
     */
    @Test
    public void test_joinFile(){
        String delimiter="\r\n-czbczbczbczbczbczbczbczbczbczbczbczbczbczbczb-\r\n";
        File file1=new File("../psd/new.txt");
        File file2=new File("../psd/new2.txt");
        Assert.assertEquals(readFileContent(czbBigText.join(delimiter,file1,file2)),
                readFileContent() + delimiter + readFileContent(file1) +
                        delimiter + readFileContent(file2));
    }

    /**
     * 连接本文内容和 joinStrs 的文本内容，形成一个新的文本文件
     */
    @Test
    public void test_joinTest(){
        String delimiter="\r\n-czbczbczbczbczbczbczbczbczbczbczbczbczbczbczb-\r\n";
        String str1="小明";
        String str2="小红";
        Assert.assertEquals(readFileContent(czbBigText.join(delimiter,str1,str2)),
                readFileContent() + delimiter + str1 +
                delimiter + str2);
    }

    /**
     * 将 str 插入到文本的 offset 索引后面
     */
    @Test
    public void test_insert(){
        boolean expectThrow=false;
        try{
            czbBigText.insert(695,"bin");
        }catch (StringIndexOutOfBoundsException e){
            expectThrow=true;
        }

        boolean faceThrow=false;
        String fileContent = readFileContent();
        try{
            new StringBuilder(fileContent).insert(695,"bin");
        }catch (StringIndexOutOfBoundsException e){
            faceThrow=true;
        }

        Assert.assertEquals(readFileContent(czbBigText.insert(0,"bin")),new StringBuilder(fileContent).insert(0,"bin").toString());
        Assert.assertEquals(readFileContent(czbBigText.insert(301,"bin")),new StringBuilder(fileContent).insert(301,"bin").toString());
        Assert.assertEquals(readFileContent(czbBigText.insert(24,"bin")),new StringBuilder(fileContent).insert(24,"bin").toString());
        Assert.assertEquals(readFileContent(czbBigText.insert(5,"bin")),new StringBuilder(fileContent).insert(5,"bin").toString());
        Assert.assertEquals(expectThrow,faceThrow);
    }

    /**
     * 将指定文本文件的文本内容 插入该文本的 offset 索引后面
     */
    @Test
    public void test_appendFile(){
        File newFile = new File("../psd/new.txt");
        Assert.assertEquals(readFileContent(czbBigText.insert(200, newFile)),
                new StringBuffer(readFileContent()).insert(200,readFileContent(newFile)).toString());
    }

    /**
     * 将该文件的文本内容的所有字母转换成小写字母
     */
    @Test
    public void test_toLowerCase(){
        Assert.assertEquals(readFileContent(czbBigText.toLowerCase()),readFileContent().toLowerCase());
    }

    /**
     * 将该文件的文本内容的所有字母转换成大写字母
     */
    @Test
    public void test_toUpperCase(){
        Assert.assertEquals(readFileContent(czbBigText.toUpperCase()),readFileContent().toUpperCase());
    }

    /**
     * 修剪该文件文本内容的开头和结尾，将开头和接口的空格去掉
     */
    @Test
    public void test_trim(){
        String trimStr = readFileContent(czbBigText.trim());
        Assert.assertFalse(trimStr.startsWith(" "));
        Assert.assertFalse(trimStr.endsWith(" "));
    }

    /**
     * 修剪该文件文本内容的开头和结尾，将开头和接口的空格，回车换行去掉
     */
    @Test
    public void test_trimNoCRLF(){
        Assert.assertEquals(readFileContent(czbBigText.trimNoCRLF()),readFileContent().trim());
    }

    /**
     * 截取在该文本从 begin 到 end 范围内的文本内容
     */
    @Test
    public void test_substr(){
        String content = readFileContent();
        Assert.assertEquals(readFileContent(czbBigText.substring(200,500)),content.substring(200,500));
        Assert.assertEquals(readFileContent(czbBigText.substring(0,1121)),content.substring(0,1121));
        Assert.assertEquals(readFileContent(czbBigText.substring(0,0)),content.substring(0,0));
    }

    /**
     * 按行读取 {@link #srcTextfile}
     * @return {@link #srcTextfile} 的文本文件内容
     */
    public String reaFileContentNoCRLF(){
        BufferedReader reader = null;
        StringBuilder sbf = new StringBuilder();
        try {
            reader =  new BufferedReader(new FileReader(srcTextfile));
            String temp;
            while ((temp = reader.readLine()) != null) {
                sbf.append(temp);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 使用字符缓存读取 {@link #srcTextfile} ，读取出来的文本内容包含\r\n
     * @return {@link #srcTextfile} 的文本文件内容
     */
    public String readFileContent() {
        FileReader reader = null;
        StringBuilder sbf = new StringBuilder();
        try {
            reader = new FileReader(srcTextfile);
            int temp;
            char[] cbuff=new char[20];
            while ((temp = reader.read(cbuff)) != -1) {
                char[] newCbuff=new char[temp];
                System.arraycopy(cbuff,0,newCbuff,0,temp);
                sbf.append(newCbuff);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 使用字符缓存读取文本文件，读取出来的文本内容包含\r\n
     * @param file 文本文件
     * @return 文本文件内容
     */
    public String readFileContent(File file){
        FileReader reader = null;
        StringBuilder sbf = new StringBuilder();
        try {
            reader = new FileReader(file);
            int temp;
            char[] cbuff=new char[20];
            while ((temp = reader.read(cbuff)) != -1) {
                char[] newCbuff=new char[temp];
                System.arraycopy(cbuff,0,newCbuff,0,temp);
                sbf.append(newCbuff);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }
}
