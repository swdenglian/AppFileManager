package dla.cn.file_manager.downLoadFile;

public class DownLoadTask {
    // 1. 通过 http 地址获取文件长度，文件名
    // 2. 通过 http 地址从sqlite 中获取任务信息
    //  2.1 如果没有就创建任务，并读取任务信息
    //  2.2 如果有读出任务信息
    // 3. 通过任务信息创建并初始化任务任务为暂停状态
    // 4. 暂停任务：销毁并保存任务信息到sqlite
    // 5. 开始任务重复上述所有过程
}
