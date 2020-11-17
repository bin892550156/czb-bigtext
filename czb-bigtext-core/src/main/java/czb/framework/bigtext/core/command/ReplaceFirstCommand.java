package czb.framework.bigtext.core.command;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 将本文第一个出现的 oldStr 覆盖成 newStr 的命令
 * <p>覆盖业务交由 {@link ReplaceCommand} 实现，这里仅实现钩子方法{@link #canReplace(long, String)}控制
 * 是否将 {@link #oldStr} 覆盖成新字符串</p>
 * @author chenzhuobin
 * @since 2020/11/16 0016
 */
public class ReplaceFirstCommand extends ReplaceCommand{

    /**
     * 旧字符串
     */
    private String oldStr;
    /**
     * 首次覆盖的标记，true 表示 还没实现第一个的覆盖
     */
    private boolean first=true;

    /**
     * 新建一个 {@link ReplaceFirstCommand} 实例
     * @param oldStr 旧字符串
     * @param newStr 新字符串
     * @param tempFile 新文本文件，该文件存放覆盖后的文本内容
     * @throws FileNotFoundException  如果 tempFile 文件不存在
     */
    public ReplaceFirstCommand(String oldStr, String newStr, File tempFile) throws FileNotFoundException {
        super(oldStr, newStr, tempFile);
        this.oldStr=oldStr;
    }

    @Override
    protected boolean canReplace(long currentReadSize, String segmentStr) {
        // 如果是否第一次覆盖 且 segmentStr 包含 oldStr 就可以进行覆盖
        if(first && segmentStr.contains(oldStr)){
            first=false;
            return true;
        }
        //只要不是第一次覆盖，都不进行覆盖
        return false;
    }
}
