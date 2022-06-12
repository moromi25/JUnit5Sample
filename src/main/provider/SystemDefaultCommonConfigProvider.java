package main.provider;

import main.constant.EnumCommonConfig;

public class SystemDefaultCommonConfigProvider {

	public int getDefaultVal() {
		// ÀÛ‚ÍDB‚È‚Ç‚©‚çæ“¾‚µ‚Ä‚­‚é
		return EnumCommonConfig.UNUSE.getVal();
	}
}
