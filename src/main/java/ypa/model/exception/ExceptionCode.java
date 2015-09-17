package ypa.model.exception;

public class ExceptionCode {

    /**
	 * 持久层异常：0
	 */
	public static String DAO_ERR = "0_DAO_ERROR";

	/**
	 * 反射异常: 1
	 */
	public static String REFLECTION_ERR = "1_REFLECTION_ERROR";

	/**
	 * 程序不支持此功能 :2
	 */
	public static String NOT_SUPPORT_ERR = "2_NOT_SUPPORT_ERROR";

	/**
	 * 日期字符串格式不正确： 3
	 */
	public static String BAD_DATE_STR_PATTERN = "3_BAD_DATE_STR_PATTERN";

	/**
	 * 冲突的受理快照，这一般是数据库中存在多条统一操作员对应的受理快照，这是不被允许的 : 4
	 */
	public static String DUMPLICATED_ACCEPT_SNAPSHOT = "4_DUMPLICATED_ACCEPT_SNAPSHOT";

	/**
	 * 移动文件失败 : 5
	 */
	public static String FILE_OPERATION_ERR_MOVE_FILE_FAILURE = "5_FILE_OPERATION_ERR_MOVE_FILE_FAILURE";

	/**
	 * 不是文件：6 一般用于文件上传
	 */
	public static String NOT_A_FILE = "6_NOT_A_FILE";

	/**
	 * 文件处于打开状态无法删除:7
	 */
	public static String FILE_IS_OPEN_CAN_NOT_BE_DELETED = "7_FILE_IS_OPEN_CAN_NOT_BE_DELETED";
	
	/**
	 * 未知的子公司s，（一般用于受理时，根据子公司获取其对应的受理上下文） ： 8
	 */
	public static String UNKNOWN_OURCOMPANY_ID = "8_UNKNOWN_OURCOMPANY_ID";
	
	/**
	 * 项目编码格式错误 ： 9
	 */
	public static String ERROR_PROJECT_CODE_EXCEPTION = "9_ERROR_PROJECT_CODE_EXCEPTION";
	
	/**
	 * 合同号不能为空： 10
	 */
	public static String CONTRANCE_NO_CAN_NOT_BE_EMPTY = "10_CONTRANCE_NO_CAN_NOT_BE_EMPTY";
	
	/**
	 * 文件不存在异常:11
	 */
	public static String FILE_DOES_NOT_EXIST = "11_FILE_DOES_NOT_EXIST";
	
	/**
	 * jpql语句不符合要求：12
	 */
	public static String BAD_JPQL_FRAGMENT = "12_BAD_JPQL_FRAGMENT";
	/**
	 * 将list为jpql参数值时出错：13
	 */
	public static String LIST_ERR_FOR_JPQL_PARAMETER = "13_LIST_ERR_FOR_JPQL_PARAMETER";

	/**
	 * 停止调度器异常
	 */
	public static final String ERROR_SHUTDOWN_SCHEDULER = "14_ERROR_SHUTDOWN_SCHEDULER";

	/**
	 * 启动调度器异常
	 */
	public static final String ERROR_STARTING_SCHEDULER = "15_ERROR_START_SCHEDULER";

	/**
	 * 创建调度器异常
	 */
	public static final String ERROR_CREATE_SCHEDULER = "16_ERROR_CREATE_SCHEDULER";

	/**
	 * 文件修改事件的事件源的类型必须是java.io.File
	 */
	public static final String FILE_CHANGED_EVENT_SOURCE_MUST_BE_TYPEOF_JAVA_IO_FILE = "17_FILE_CHANGED_EVENT_SOURCE_MUST_BE_TYPEOF_JAVA_IO_FILE";

	/**
	 * quartz
	 * 更新指定触发器的的克隆表达式异常
	 */
	public static final String ERROR_UPDATING_CRON_EXPRESSION = "18_ERROR_UPDATING_CRON_EXPRESSION_OF_QUARTZ";

	public static final String ERROR_PAUSING_TRIGGER = "19_ERROR_PAUSING_TRIGGER";

	/**
	 * IO EXCEPTION : 20
	 */
	public static final String IO_EXCEPTION = "20_IO_EXCEPTION";

	/**
	 * 致命异常
	 */
	public static String FATAL_ERROR = "999999_FATAL_ERROR";


	/**
	 * 持久单元名称错误 21
	 */
	public static final String ERROR_PERSITENCE_UNIT_NAME = "21_ERROR_PERSITENCE_UNIT_NAME";

	/**
	 * 未知应用服务器 22
	 */
	public static final String UNKOWN_APP_SERVER_ERROR = "22_UNKOWN_APP_SERVER_ERROR";
    /**
     * 复制文件出错 23
     */
    public static final String FILE_COPY_ERROR = "23_FILE_COPY_ERROR";
}
