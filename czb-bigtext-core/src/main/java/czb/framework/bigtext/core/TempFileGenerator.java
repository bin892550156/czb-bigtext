package czb.framework.bigtext.core;

import java.io.File;

/**
 * 临时文件生成器
 * @author chenzhuobin
 * @since 2020/11/17 0017
 */
public class TempFileGenerator {

    /**
     * 源文件
     */
    private File srcFile;

    /**
     * 新建一个 {@link TempFileGenerator} 生成器
     * @param srcFile 源文件
     */
    public TempFileGenerator(File srcFile) {
        this.srcFile = srcFile;
    }

    /**
     * 生成临时文件
     * <p>在 {@link #srcFile} 所在目录生成</p>
     * @return 临时文件对象
     */
    public File getTempFile(){
        return new File(srcFile.getAbsolutePath()+".temp");
    }

    /**
     * 生成临时文件
     * <p>在 {@link #srcFile} 所在目录生成</p>
     * @param fileNum 文件编号
     * @return 临时文件对象
     */
    public File getTempFile(int fileNum){
        return new File(srcFile.getAbsolutePath()+".temp"+fileNum);
    }
}
