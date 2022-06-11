package main;

import main.constant.EnumCommonConfig;
import main.provider.SystemDefaultCommonConfigProvider;

public class CanUseService {

	public boolean canManageEachConfigByEmployee(EnumCommonConfig config, SystemDefaultCommonConfigProvider provider) {
		return false;
	}
}
