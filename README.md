# 超大文本处理框架
这个一个针对超大文本文件的处理框架，适合那些超过100M的文本文件，如日志文件。该文件是按指定
容器字符数来处理文本内容，所以最大程度避免了操作这些大文本文件时的OOM问题。这种按指定容器字符
数读取到内容我统一位段。

## 关于查找读取
在读取文本的时候，会推断读取的文本的尾部有没有 要查找的字符串 的开头部分， 有就需要将 要查找的字符串
剩余的字符读取到该段文字里

为了尽可能保证读取不会因为读取策略导致OOM的问题，合适的段长度应该为 段大小 - 要查找的字符串大小 的差 ，这样会导致读取速度很慢，
但是这样比较安全。

虽然 segmentSize - toFindStrLength 的差比较安全，但是差值出现下列情况时，还是会导致段文本在找到要查找的文本时， 
大于 segmentSize 的情况：
1. 如果 segmentSize == toFindStrLength || 如果 segmentSize < toFindStrLength 时， 则为 segmentSize/5 。
因为至少要读点数据才能保证文本的读取正常
2. 如果 toFindStrLength > 2*segmentSize,则为 toFindStrLength,因为太大了，没有足够的大小承载要查找的文本，
但是为了保证 功能的可用性，只能用 toFindStrLength.

## 用法
### maven 配置
该项目并没有放到maven的仓库，所以需要拉取本项目，在根目录下执行 `mvn clean install`.
```xml
   <dependency>
       <groupId>czb.framework</groupId>
       <artifactId>czb-bigtext-core</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
```
### 用例
```java
/**
 * {@link czb.framework.bigtext.core.CzbBigText} 的测试用例
 */
public class CzbBigTextTest {

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

    @Test
    public void test_indexOf(){
        String target="防身的武器";
        String content = readFileContent();
        Assert.assertEquals(czbBigText.indexOf(target),content.indexOf(target));
        Assert.assertEquals(czbBigText.indexOf(target,10),content.indexOf(target,10));
    }

    @Test
    public void test_length(){
        Assert.assertEquals(czbBigText.length(),readFileContent().length());
    }

    @Test
    public void test_lengthNoCRLF(){
        Assert.assertEquals(czbBigText.lengthNoCRLF(),reaFileContentNoCRLF().length());
    }

    @Test
    public void test_readFileContent(){
        String content = readFileContent();
        System.out.println(content);
    }

    @Test
    public void test_detemineTailAppendCharCount(){
        String source="hello world";
        String target="ldff";
        ReadFileDelegate delegate=new ReadFileDelegate(srcTextfile,20,"utf-8");
        Assert.assertEquals(delegate.getMatchRemainCount(source.toCharArray(),target.toCharArray()),2);
    }


    @Test
    public void test_replace(){
        String oldStr="如乌龟形的、山形的、触须形的……还有五颜六色的花朵，如鲜红的，嫩黄的，湛蓝的……我们家族的";
        String newStr="what，你在说什么？";
        Assert.assertEquals(readFileContent(czbBigText.replace(oldStr,newStr)), readFileContent().replace(oldStr,newStr ));
    }

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

    @Test
    public void test_joinFile(){
        String delimiter="\r\n-czbczbczbczbczbczbczbczbczbczbczbczbczbczbczb-\r\n";
        File file1=new File("../psd/new.txt");
        File file2=new File("../psd/new2.txt");
        Assert.assertEquals(readFileContent(czbBigText.join(delimiter,file1,file2)),
                readFileContent() + delimiter + readFileContent(file1) +
                        delimiter + readFileContent(file2));
    }

    @Test
    public void test_joinTest(){
        String delimiter="\r\n-czbczbczbczbczbczbczbczbczbczbczbczbczbczbczb-\r\n";
        String str1="小明";
        String str2="小红";
        Assert.assertEquals(readFileContent(czbBigText.join(delimiter,str1,str2)),
                readFileContent() + delimiter + str1 +
                delimiter + str2);
    }

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

    @Test
    public void test_appendFile(){
        File newFile = new File("../psd/new.txt");
        Assert.assertEquals(readFileContent(czbBigText.insert(200, newFile)),
                new StringBuffer(readFileContent()).insert(200,readFileContent(newFile)).toString());
    }

    @Test
    public void test_toLowerCase(){
        Assert.assertEquals(readFileContent(czbBigText.toLowerCase()),readFileContent().toLowerCase());
    }

    @Test
    public void test_toUpperCase(){
        Assert.assertEquals(readFileContent(czbBigText.toUpperCase()),readFileContent().toUpperCase());
    }

    @Test
    public void test_trim(){
        String trimStr = readFileContent(czbBigText.trim());
        Assert.assertFalse(trimStr.startsWith(" "));
        Assert.assertFalse(trimStr.endsWith(" "));
    }

    @Test
    public void test_trimNoCRLF(){
        Assert.assertEquals(readFileContent(czbBigText.trimNoCRLF()),readFileContent().trim());
    }

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
        StringBuffer sbf = new StringBuffer();
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

```

