package main.provider;

import main.constant.EnumCommonConfig;

public class SystemDefaultCommonConfigProvider {

	public int getDefaultVal() {
		// ���ۂ�DB�Ȃǂ���擾���Ă���
		return EnumCommonConfig.UNUSE.getVal();
	}
}
