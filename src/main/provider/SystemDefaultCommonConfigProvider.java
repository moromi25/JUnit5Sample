package main.provider;

import main.constant.EnumCommonConfig;

public class SystemDefaultCommonConfigProvider {

	public int getDefaultVal() {
		return EnumCommonConfig.UNUSE.getVal();
	}
}
