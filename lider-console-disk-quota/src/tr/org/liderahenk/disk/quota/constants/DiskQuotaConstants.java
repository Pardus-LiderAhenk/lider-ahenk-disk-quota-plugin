package tr.org.liderahenk.disk.quota.constants;

public class DiskQuotaConstants {
	
	public static final String PLUGIN_NAME = "disk-quota";
	
	public static final String PLUGIN_VERSION = "1.0.0";
	
	public static final int MAX_VALUE = 50000;  //TODO max value??
	
	public static final int MIN_VALUE = 0;  //TODO min value??
	
	public static final class PARAMETERS {
		public static final String SOFT_QUOTA = "soft-quota";
		public static final String HARD_QUOTA = "hard-quota";
		public static final String DEFAULT_QUOTA = "default-quota";
	}
	
}